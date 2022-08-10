package com.temnenkov.actor

import com.temnenkov.leventactor.LoggedLeventActor
import com.temnenkov.utils.fromJson
import com.temnenkov.utils.toJson

class GameFacadeActor : LoggedLeventActor() {
    override fun handleMessage(from: String?, me: String, payload: String): OutMessage {
        val message = payload.fromJson(GameFacadeInboundMessage::class.java)
        return OutMessage(
            ActorAddress.ADAPTER_GAMEFACADE_TELEGRAM,
            AdapterGameFacadeTelegramActor.AdapterGameFacadeTelegramMessage(
                message.from,
                "Пока не могу обработать '${message.message}'"
            ).toJson()
        )
    }

    data class GameFacadeInboundMessage(val from: String, val message: String)
}
