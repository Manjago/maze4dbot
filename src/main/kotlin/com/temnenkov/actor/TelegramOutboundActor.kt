package com.temnenkov.actor

import com.temnenkov.db.QueueDb
import com.temnenkov.db.StoreDb
import com.temnenkov.leventactor.LeventActor
import com.temnenkov.leventbus.LeventMessage
import com.temnenkov.telegram.TelegramBot
import com.temnenkov.utils.fromJson

class TelegramOutboundActor(private val telegramBot: TelegramBot) : LeventActor {
    override fun handleMessage(leventMessage: LeventMessage, storeDb: StoreDb, queueDb: QueueDb) {
        if (leventMessage.payload != null) {
            val message = leventMessage.payload.fromJson(TelegramBot.SendMessageRequest::class.java)
            telegramBot.sendMessage(message)
        }
        queueDb.done(leventMessage.id)
    }
}
