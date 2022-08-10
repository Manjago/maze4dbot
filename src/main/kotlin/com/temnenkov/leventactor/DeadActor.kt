package com.temnenkov.leventactor

import com.temnenkov.db.QueueDb
import com.temnenkov.db.StoreDb
import com.temnenkov.leventbus.LeventMessage
import mu.KotlinLogging
import java.time.Instant

class DeadActor : LeventActor {
    override fun handleMessage(leventMessage: LeventMessage, storeDb: StoreDb, queueDb: QueueDb): List<Pair<LeventMessage, Instant>>? {
        logger.warn { "lost message $leventMessage" }
        return null
    }

    companion object {
        private val logger = KotlinLogging.logger { }
    }
}
