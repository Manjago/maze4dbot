package com.temnenkov.leventbus

import com.aventrix.jnanoid.jnanoid.NanoIdUtils
import com.temnenkov.levent.LeventProperties
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Duration
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class XodusLeventBusTest {

    private lateinit var leventBus: XodusLeventBus

    @BeforeEach
    internal fun setUp() {
        val properties = System.getProperties()
        properties.setProperty(LeventProperties.LB_DATABASE, "target/.xodus-${NanoIdUtils.randomNanoId()}")

        leventBus = XodusLeventBus()
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
        leventBus.push(pushedMessage)

        leventBus.checkQueueElement(1, 0, id, pushedMessage)

        val due = leventBus.indexElementDate(1, 0, id)

        val pulledMessage = leventBus.pull()
        assertEquals(pushedMessage, pulledMessage)

        leventBus.checkIndexElement(1, 0, id, due.plus(maxDuration))
        leventBus.checkQueueElement(1, 0, id, pushedMessage)

        leventBus.done(id)

        leventBus.checkIndexElement(1, 0, id, due.plus(maxDuration))
        leventBus.checkEmptyQueue()

        assertNull(leventBus.pull((due.plus(maxDuration).plus(maxDuration))))

        leventBus.checkEmptyIndex()
        leventBus.checkEmptyQueue()
    }
}
