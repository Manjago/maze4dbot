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
                queueDb.push(
                    LeventMessage(
                        from = leventMessage.to,
                        to = out.to,
                        payload = out.payload
                    ).also {
                        logger.info { "sent message $it" }
                    }
                )
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
