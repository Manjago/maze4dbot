package com.temnenkov.leventbus

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.Properties
import kotlin.test.assertNull

internal class XodusLeventBusTest {

    private lateinit var leventBus: XodusLeventBus

    @BeforeEach
    internal fun setUp() {
        leventBus = XodusLeventBus(
            Properties().apply {
                put("database", "target/.leventbusTestData")
            }
        )
    }

    @AfterEach
    internal fun tearDown() {
        leventBus.dropAll()
    }

    @Test
    internal fun noData() {
        assertNull(leventBus.pull())
    }
}
