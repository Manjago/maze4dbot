package com.temnenkov.actor

import com.temnenkov.leventactor.LoggedLeventActor
import com.temnenkov.telegram.TelegramBot
import com.temnenkov.utils.fromJson
import com.temnenkov.utils.toJson

class AdapterGameFacadeTelegramActor : LoggedLeventActor() {
    override fun handleMessage(from: String?, me: String, payload: String): OutMessage {
        val incomingMessage = payload.fromJson(AdapterGameFacadeTelegramMessage::class.java)
        return OutMessage(ActorAddress.TELEGRAM_OUTBOUND, TelegramBot.SendMessageRequest(incomingMessage.to.toLong(), incomingMessage.message, incomingMessage.replyTo).toJson())
    }

    data class AdapterGameFacadeTelegramMessage(val to: String, val message: String, val replyTo: Long?)
}
