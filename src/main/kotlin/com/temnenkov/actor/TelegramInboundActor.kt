package com.temnenkov.actor

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

    override fun handleMessage(leventMessage: LeventMessage, storeDb: StoreDb): List<Pair<LeventMessage, Instant>>? {
        if (!worker.compareAndExchange(false, true)) {
            try {
                val offset = (storeDb.get("TelegramInboundActor", "offset") ?: "-1").toLong()

                val updates = telegramBot.getUpdates(offset + 1)
                logger.info { "get updates $updates" }
                val result = mutableListOf<Pair<LeventMessage, Instant>>()
                if (updates.isNotEmpty()) {
                    val newOffset = updates.maxBy { it.updateId }.updateId
                    logger.info { "set new offset = $newOffset" }
                    storeDb.put("TelegramInboundActor", "offset", newOffset.toString())

                    updates.forEach {
                        val outMessage = LeventMessage(
                            from = leventMessage.to,
                            to = ActorAddress.ADAPTER_TELEGRAM_GAMEFACADE,
                            payload = it.toJson()
                        )
                        logger.info { "wanna to send to ${outMessage.to} payload ${outMessage.payload}" }
                        result.add(outMessage to Instant.now())
                    }
                }
                result.add(
                    myMessage() to
                        Instant.now().plusMillis(1000L)
                )

                return result.toList()
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
        fun myMessage() = LeventMessage(
            to = ActorAddress.TELEGRAM_INBOUND,
            maxDuration = Duration.ofSeconds(TelegramBot.longPollingTimeout() + 10),
            payload = "{}"
        )
    }
}
