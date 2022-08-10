package com.temnenkov.leventactor

import com.temnenkov.db.QueueDb
import com.temnenkov.db.StoreDb
import com.temnenkov.leventbus.LeventMessage
import mu.KotlinLogging
import java.time.Instant

abstract class LoggedLeventActor : LeventActor {

    abstract fun handleMessage(from: String?, me: String, payload: String): OutMessage?

    override fun handleMessage(leventMessage: LeventMessage, storeDb: StoreDb, queueDb: QueueDb): List<Pair<LeventMessage, Instant>>? {
        logger.info { "got message $leventMessage" }
        val out = handleMessage(leventMessage.from, leventMessage.to, leventMessage.payload)
        return if (out != null) {
            val storedMessage = LeventMessage(
                from = leventMessage.to,
                to = out.to,
                payload = out.payload
            )
            logger.info { "wanna to send message $storedMessage" }
            listOf(storedMessage to Instant.now())
        } else {
            null
        }
    }

    data class OutMessage(val to: String, val payload: String)

    companion object {
        private val logger = KotlinLogging.logger { }
    }
}
