package com.temnenkov.actor

import com.temnenkov.leventactor.LoggedLeventActor
import com.temnenkov.telegram.TelegramBot
import com.temnenkov.utils.fromJson

class TelegramOutboundActor(private val telegramBot: TelegramBot) : LoggedLeventActor() {
    override fun handleMessage(from: String?, me: String, payload: String): OutMessage? {
        val message = payload.fromJson(TelegramBot.SendMessageRequest::class.java)
        telegramBot.sendMessage(message)
        return null
    }
}
