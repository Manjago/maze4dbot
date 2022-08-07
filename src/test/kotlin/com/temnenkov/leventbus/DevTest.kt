package com.temnenkov.leventbus

import com.temnenkov.telegram.TelegramBot
import com.temnenkov.utils.enrichSystemProperties
import org.junit.jupiter.api.Test

class DevTest {
    @Test
    internal fun dev() {
        enrichSystemProperties("src/test/kotlin/com/temnenkov/leventbus/dev.properties")
        val result = TelegramBot().getUpdates(0)
        println(result)
    }
}
