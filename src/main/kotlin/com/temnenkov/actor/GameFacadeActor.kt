package com.temnenkov.actor

import com.temnenkov.db.QueueDb
import com.temnenkov.db.StoreDb
import com.temnenkov.leventactor.LeventActor
import com.temnenkov.leventbus.LeventMessage
import com.temnenkov.utils.fromJson
import com.temnenkov.utils.toJson

class GameFacadeActor : LeventActor {
    override fun handleMessage(leventMessage: LeventMessage, storeDb: StoreDb, queueDb: QueueDb) {
        if (leventMessage.payload != null) {
            val message = leventMessage.payload.fromJson(GameFacadeInboundMessage::class.java)
            queueDb.push(
                LeventMessage(
                    from = leventMessage.to,
                    to = ActorAddress.ADAPTER_GAMEFACADE_TELEGRAM,
                    payload = AdapterGameFacadeTelegramActor.AdapterGameFacadeTelegramMessage(
                        message.from,
                        "Пока не могу обработать '${message.message}'"
                    ).toJson()
                )
            )
        }

        queueDb.done(leventMessage.id)
    }

    data class GameFacadeInboundMessage(val from: String, val message: String)
}
