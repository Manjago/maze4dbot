package com.temnenkov.leventbus

import com.aventrix.jnanoid.jnanoid.NanoIdUtils
import com.temnenkov.db.XodusQueueDb
import com.temnenkov.levent.LeventProperties
import jetbrains.exodus.env.Environment
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class XodusLeventBusTest {

    private lateinit var leventBus: XodusLeventBus
    private lateinit var environment: Environment

    @BeforeEach
    internal fun setUp() {
        val properties = System.getProperties()
        properties.setProperty(LeventProperties.LB_DATABASE, "target/.xodus-${NanoIdUtils.randomNanoId()}")

        val (env, _) = createEnvironment(properties)
        environment = env

        leventBus = XodusLeventBus(environment)
    }

    @Test
    internal fun noData() {
        assertNull(leventBus.pull())
    }

    @Test
    internal fun pullAndDone() {
        val id = "1"
        val maxDuration = Duration.ofSeconds(5)

        val pushedMessage = LeventMessage(id, "2", "3", "4", maxDuration)
        push(pushedMessage)

        leventBus.checkQueueElement(1, 0, id, pushedMessage)

        val due = leventBus.indexElementDate(1, 0, id)

        val pulledMessage = leventBus.pull()
        assertEquals(pushedMessage, pulledMessage)

        leventBus.checkIndexElement(1, 0, id, due.plus(maxDuration))
        leventBus.checkQueueElement(1, 0, id, pushedMessage)

        done(id)

        leventBus.checkIndexElement(1, 0, id, due.plus(maxDuration))
        leventBus.checkEmptyQueue()

        assertNull(leventBus.pull((due.plus(maxDuration).plus(maxDuration))))

        leventBus.checkEmptyIndex()
        leventBus.checkEmptyQueue()
    }

    private fun push(message: LeventMessage) = environment.executeInTransaction { txn ->
        XodusQueueDb(environment, txn).push(message, Instant.now())
    }

    private fun done(messageId: String) = environment.executeInTransaction { txn ->
        XodusQueueDb(environment, txn).done(messageId)
    }
}
