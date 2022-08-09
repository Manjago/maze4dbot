package com.temnenkov.leventactor

import com.temnenkov.db.QueueDb
import com.temnenkov.db.StoreDb
import com.temnenkov.leventbus.LeventMessage
import mu.KotlinLogging

class DeadActor : LeventActor {
    override fun handleMessage(leventMessage: LeventMessage, storeDb: StoreDb, queueDb: QueueDb) {
        queueDb.done(leventMessage.id)
        logger.warn { "lost message $leventMessage" }
    }

    companion object {
        private val logger = KotlinLogging.logger { }
    }
}
