package com.temnenkov.actor

import com.temnenkov.leventactor.LoggedLeventActor
import com.temnenkov.telegram.TelegramBot
import com.temnenkov.utils.fromJson
import com.temnenkov.utils.toJson

class AdapterTelegramGameFacadeActor : LoggedLeventActor() {
    override fun handleMessage(from: String?, me: String, payload: String): OutMessage {
        val message = payload.fromJson(TelegramBot.IncomingMessage::class.java)
        return OutMessage(ActorAddress.GAMEFACADE, GameFacadeActor.GameFacadeInboundMessage(message.from.toString(), message.text, message.messageId).toJson())
    }
}
