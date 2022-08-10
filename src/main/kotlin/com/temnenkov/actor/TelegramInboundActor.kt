package com.temnenkov.actor

import com.temnenkov.db.QueueDb
import com.temnenkov.db.StoreDb
import com.temnenkov.leventactor.LeventActor
import com.temnenkov.leventbus.LeventMessage
import com.temnenkov.telegram.TelegramBot
import com.temnenkov.utils.toJson
import mu.KotlinLogging
import java.time.Duration
import java.time.Instant
import java.util.concurrent.atomic.AtomicBoolean

class TelegramInboundActor(private val telegramBot: TelegramBot) : LeventActor {

    private val worker = AtomicBoolean(false)
    private var offset = -1L

    override fun handleMessage(leventMessage: LeventMessage, storeDb: StoreDb, queueDb: QueueDb): List<Pair<LeventMessage, Instant>>? {
        if (!worker.compareAndExchange(false, true)) {
            try {
                val updates = telegramBot.getUpdates(offset + 1)
                logger.info { "get updates $updates" }
                if (updates.isNotEmpty()) {
                    offset = updates.maxBy { it.updateId }.updateId
                    logger.info { "set new offset = $offset" }

                    updates.forEach {
                        val outMessage = LeventMessage(
                            from = leventMessage.to,
                            to = ActorAddress.ADAPTER_TELEGRAM_GAMEFACADE,
                            payload = it.toJson()
                        )
                        logger.info { "wanna to send to ${outMessage.to} payload ${outMessage.payload}" }
                        queueDb.push(outMessage)
                        logger.info { "sent ${outMessage.id} to ${outMessage.to} payload ${outMessage.payload}" }
                        val stored = queueDb.getMessageFromQueue(outMessage.id)
                        if (stored == null) {
                            logger.error { "NOT STORED $outMessage !!!" }
                        } else {
                            logger.info { "stored ok" }
                        }
                    }
                }

                logger.info { "point 1" }
                logger.info { "point 2" }
                return listOf(
                    LeventMessage(
                        to = ActorAddress.TELEGRAM_INBOUND,
                        maxDuration = Duration.ofSeconds(TelegramBot.longPollingTimeout() + 10)
                    ) to
                        Instant.now().plusMillis(1000L)
                )
            } finally {
                logger.info { "point 4" }
                worker.set(false)
                logger.info { "point 5" }
            }
        } else {
            logger.info { "$leventMessage drop" }
            return null
        }
    }

    companion object {
        private val logger = KotlinLogging.logger { }
    }
}
