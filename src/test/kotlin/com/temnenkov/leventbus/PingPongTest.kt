package com.temnenkov.leventbus

import com.aventrix.jnanoid.jnanoid.NanoIdUtils
import com.temnenkov.db.QueueDb
import com.temnenkov.db.StoreDb
import com.temnenkov.levent.LeventProperties
import com.temnenkov.leventactor.DeadActor
import com.temnenkov.leventactor.LeventActor
import com.temnenkov.leventactor.leventLoop
import com.temnenkov.utils.get
import com.temnenkov.utils.push
import mu.KotlinLogging
import org.awaitility.Awaitility
import org.awaitility.kotlin.has
import org.awaitility.kotlin.untilCallTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.Instant
import kotlin.test.assertEquals

internal class PingPongTest {

    @BeforeEach
    internal fun setUp() {
        val properties = System.getProperties()
        properties.setProperty(LeventProperties.LB_DATABASE, "target/.xodus-${NanoIdUtils.randomNanoId()}")
    }

    class PongActor(private val id: String) : LeventActor {

        override fun handleMessage(leventMessage: LeventMessage, storeDb: StoreDb, queueDb: QueueDb) {
            val other = leventMessage.from
            val intValue = leventMessage.payload?.toInt() ?: 0

            logger.info { "pong $id got $intValue from $other" }

            val trace = storeDb.get("PongActor", id)
            val newTrace = if (trace == null) {
                leventMessage.payload ?: "0"
            } else {
                trace + (leventMessage.payload ?: "0")
            }
            storeDb.put("PongActor", id, newTrace)

            if (intValue <= 0) {
                logger.info { "pong $id done with $intValue" }
                queueDb.done(leventMessage.id)
                return
            }

            val newIntValue = intValue - 1
            logger.info { "pong $id wanna send $newIntValue to $other" }

            queueDb.done(leventMessage.id)
            if (other != null) {
                queueDb.push(
                    message = LeventMessage(
                        NanoIdUtils.randomNanoId(),
                        id,
                        other,
                        (intValue - 1).toString(),
                        Duration.ofSeconds(5)
                    ),
                    due = Instant.now().plusMillis(200)
                )
            }
        }

        companion object {
            private val logger = KotlinLogging.logger { }
            const val COLLECTION = "PongActor"
        }
    }

    @Test
    internal fun pingPong() {
        val (environment, loopStep) = createEnvironment()

        val bus = XodusLeventBus(environment)

        environment.executeInTransaction { txn ->
            environment.push(LeventMessage(NanoIdUtils.randomNanoId(), "1", "2", "5", Duration.ofSeconds(5)))
        }

        leventLoop(
            3,
            "bus-",
            bus,
            mapOf("1" to PongActor("1"), "2" to PongActor("2")),
            environment,
            loopStep,
            DeadActor()
        )

        Awaitility.await().atMost(Duration.ofSeconds(20L))
            .untilCallTo {
                bus.dumpQueueToList()
            }.has { this.isEmpty() }

        assertEquals("420", environment.get(PongActor.COLLECTION, "1"))
        assertEquals("531", environment.get(PongActor.COLLECTION, "2"))
    }
}
