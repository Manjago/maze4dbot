package com.temnenkov.leventactor

import com.temnenkov.db.QueueDb
import com.temnenkov.db.StoreDb
import com.temnenkov.leventbus.LeventMessage
import mu.KotlinLogging

abstract class LoggedLeventActor : LeventActor {

    abstract fun handleMessage(from: String?, me: String, payload: String): OutMessage?

    override fun handleMessage(leventMessage: LeventMessage, storeDb: StoreDb, queueDb: QueueDb) {
        logger.info { "got message $leventMessage" }
        if (leventMessage.payload != null) {
            val out = handleMessage(leventMessage.from, leventMessage.to, leventMessage.payload)
            if (out != null) {
                val storedMessage = LeventMessage(
                    from = leventMessage.to,
                    to = out.to,
                    payload = out.payload
                )
                logger.info { "wanna to send message $storedMessage" }
                queueDb.push(storedMessage)
                logger.info { "sent $storedMessage" }
                val stored = queueDb.getMessageFromQueue(storedMessage.id)
                if (stored == null) {
                    logger.error { "NOT STORED $storedMessage !!!" }
                }
            }
        }
        queueDb.done(leventMessage.id)
        logger.info { "done message $leventMessage" }
    }

    data class OutMessage(val to: String, val payload: String)

    companion object {
        private val logger = KotlinLogging.logger { }
    }
}
