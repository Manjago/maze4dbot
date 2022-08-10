package com.temnenkov.actor

import com.temnenkov.db.QueueDb
import com.temnenkov.db.StoreDb
import com.temnenkov.leventactor.LeventActor
import com.temnenkov.leventbus.LeventMessage
import com.temnenkov.telegram.TelegramBot
import mu.KotlinLogging
import java.time.Duration
import java.time.Instant
import java.util.concurrent.atomic.AtomicBoolean

class TelegramInboundActor(private val telegramBot: TelegramBot) : LeventActor {

    private val worker = AtomicBoolean(false)
    private var offset = -1L

    override fun handleMessage(leventMessage: LeventMessage, storeDb: StoreDb, queueDb: QueueDb) {
        if (!worker.compareAndExchange(false, true)) {
            try {
                val updates = telegramBot.getUpdates(offset + 1)
                logger.info { "get updates $updates" }
                if (updates.isNotEmpty()) {
                    offset = updates.maxBy { it.updateId }.updateId
                    logger.info { "set new offset = $offset" }
                }
                queueDb.done(leventMessage.id)
                queueDb.push(
                    LeventMessage(
                        to = ActorAddress.TELEGRAM_INBOUND,
                        maxDuration = Duration.ofSeconds(TelegramBot.longPollingTimeout() + 10)
                    ),
                    Instant.now().plusMillis(1000L)
                )
            } finally {
                worker.set(false)
            }
        } else {
            logger.info { "$leventMessage drop" }
        }
    }

    companion object {
        private val logger = KotlinLogging.logger { }
    }
}
