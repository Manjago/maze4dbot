package com.temnenkov.actor

import com.temnenkov.db.StoreDb
import com.temnenkov.leventactor.BaseBlockedLeventActor
import com.temnenkov.leventbus.LeventMessage
import com.temnenkov.telegram.TelegramBot
import com.temnenkov.utils.toJson
import mu.KotlinLogging
import java.time.Duration
import java.time.Instant

class TelegramInboundActor(private val telegramBot: TelegramBot) : BaseBlockedLeventActor() {

    override fun handleMessage(leventMessage: LeventMessage, storeDb: StoreDb): List<Pair<LeventMessage, Instant>> {
        val offset = (storeDb.get("TelegramInboundActor", "offset") ?: "-1").toLong()

        val (updates, maxOffset) = telegramBot.getUpdates(offset + 1)
        if (maxOffset != null) {
            logger.info { "set new offset = $maxOffset" }
            storeDb.put("TelegramInboundActor", "offset", maxOffset.toString())
        }

        logger.info { "get updates $updates" }
        val result = mutableListOf<Pair<LeventMessage, Instant>>()
        updates.forEach {
            val outMessage = LeventMessage(
                from = leventMessage.to,
                to = ActorAddress.ADAPTER_TELEGRAM_GAMEFACADE,
                payload = it.toJson()
            )
            logger.info { "wanna to send to ${outMessage.to} payload ${outMessage.payload}" }
            result.add(outMessage to Instant.now())
        }
        result.add(
            myMessage() to Instant.now().plusMillis(1000L)
        )

        return result.toList()
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
