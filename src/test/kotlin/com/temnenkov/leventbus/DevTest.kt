package com.temnenkov.leventbus

import com.temnenkov.telegram.TelegramBot
import com.temnenkov.utils.enrichSystemProperties
import org.junit.jupiter.api.Test

class DevTest {
    @Test
    internal fun dev() {
        enrichSystemProperties("src/test/kotlin/com/temnenkov/leventbus/dev.properties")
        val telegramBot = TelegramBot()
        val result = telegramBot.getUpdates(496209290)
        println(result)

        result.forEach {
            println("wanna sent to ${it.from} messageId ${it.messageId}")
            val status = telegramBot.sendReplyMessage(it.from, it.messageId, "Вижу '${it.text}' - но я пока в разработке, ничего не сделаю")
            println("sent $status")
        }
    }

    private fun TelegramBot.sendReplyMessage(to: Long, replyToMessageId: Long, text: String): TelegramBot.SendMessageResponse? =
        sendMessage(TelegramBot.SendMessageRequest(to, text, replyToMessageId))
}
