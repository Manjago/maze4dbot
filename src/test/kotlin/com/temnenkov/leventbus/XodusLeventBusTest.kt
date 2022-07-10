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
        val pushedMessage = LeventMessage("1", "2", "3", "4", Duration.ofSeconds(5))
        leventBus.push(pushedMessage)

        val pulledMessage = leventBus.pull()
        assertEquals(pushedMessage, pulledMessage)
        // todo сделать тест похитрее - с повторами
    }
}
