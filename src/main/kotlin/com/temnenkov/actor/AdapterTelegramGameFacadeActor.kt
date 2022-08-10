package com.temnenkov.actor

import com.temnenkov.db.QueueDb
import com.temnenkov.db.StoreDb
import com.temnenkov.leventactor.LeventActor
import com.temnenkov.leventbus.LeventMessage
import com.temnenkov.telegram.TelegramBot
import com.temnenkov.utils.fromJson
import com.temnenkov.utils.toJson

class AdapterTelegramGameFacadeActor : LeventActor {
    override fun handleMessage(leventMessage: LeventMessage, storeDb: StoreDb, queueDb: QueueDb) {
        if (leventMessage.payload != null) {
            val incomingMessage = leventMessage.payload.fromJson(TelegramBot.IncomingMessage::class.java)
            queueDb.push(
                LeventMessage(
                    from = leventMessage.to,
                    to = ActorAddress.GAMEFACADE,
                    payload = GameFacadeActor.GameFacadeInboundMessage(incomingMessage.from.toString(), incomingMessage.text).toJson()
                )
            )
        }

        queueDb.done(leventMessage.id)
    }
}
