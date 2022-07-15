package com.temnenkov.leventbus

import com.aventrix.jnanoid.jnanoid.NanoIdUtils
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Duration
import java.util.Properties
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class XodusLeventBusTest {

    private lateinit var leventBus: XodusLeventBus

    @BeforeEach
    internal fun setUp() {
        leventBus = XodusLeventBus(
            Properties().apply {
                put("database", "target/.xodus-${NanoIdUtils.randomNanoId()}")
            }
        )
    }

    @Test
    internal fun noData() {
        assertNull(leventBus.pull())
    }

    @Test
    internal fun oneItem() {
        val id = "1"
        val maxDuration = Duration.ofSeconds(5)

        val pushedMessage = LeventMessage(id, "2", "3", "4", maxDuration)
        leventBus.push(pushedMessage)

        val due = with(leventBus.dumpIndexToList()) {
            assertEquals(1, this.size)
            assertEquals(id, this[0].second)
            this[0].first
        }


        val pulledMessage = leventBus.pull()
        assertEquals(pushedMessage, pulledMessage)

        with(leventBus.dumpIndexToList()) {
            assertEquals(1, this.size)
            assertEquals(id, this[0].second)
            assertEquals(due.plus(maxDuration), this[0].first)
        }
    }
}
