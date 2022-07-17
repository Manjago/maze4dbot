package com.temnenkov.leventbus

import com.aventrix.jnanoid.jnanoid.NanoIdUtils
import com.temnenkov.levent.LeventProperties
import com.temnenkov.leventactor.LeventActor
import com.temnenkov.leventactor.leventLoop
import jetbrains.exodus.env.Environment
import jetbrains.exodus.env.Transaction
import mu.KotlinLogging
import org.awaitility.Awaitility
import org.awaitility.kotlin.has
import org.awaitility.kotlin.untilCallTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.Instant

internal class PingPongTest {

    @BeforeEach
    internal fun setUp() {
        val properties = System.getProperties()
        properties.setProperty(LeventProperties.LB_DATABASE, "target/.xodus-${NanoIdUtils.randomNanoId()}")
    }

    class PongActor(private val id: String) : LeventActor<Transaction, Environment> {

        override fun handleMessage(leventMessage: LeventMessage, bus: LeventBusForActor<Transaction, Environment>) {
            val other = leventMessage.from
            val intValue = leventMessage.payload.toInt()

            logger.info { "pong $id got $intValue from $other" }

            if (intValue <= 0) {
                logger.info { "pong $id done with $intValue" }
                bus.done(leventMessage.id)
                return
            }

            val newIntValue = intValue - 1
            logger.info { "pong $id wanna send $newIntValue to $other" }

            bus.push(
                message = LeventMessage(
                    NanoIdUtils.randomNanoId(),
                    id,
                    other,
                    (intValue - 1).toString(),
                    Duration.ofSeconds(5)
                ),
                doneMesssageId = leventMessage.id,
                due = Instant.now().plusMillis(200)
            )
        }

        companion object {
            private val logger = KotlinLogging.logger { }
        }
    }

    @Test
    internal fun pingPong() {
        val bus = XodusLeventBus()

        bus.push(LeventMessage(NanoIdUtils.randomNanoId(), "1", "2", "5", Duration.ofSeconds(5)))

        leventLoop(
            3,
            "bus-",
            bus,
            mapOf("1" to PongActor("1"), "2" to PongActor("2"))
        )

        Awaitility.await().atMost(Duration.ofSeconds(20L))
            .untilCallTo {
                bus.dumpQueueToList()
            }.has { this.isEmpty() }

        // todo тест на состояние
    }
}
