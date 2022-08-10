package com.temnenkov

import com.temnenkov.actor.ActorAddress
import com.temnenkov.actor.AdapterGameFacadeTelegramActor
import com.temnenkov.actor.AdapterTelegramGameFacadeActor
import com.temnenkov.actor.GameFacadeActor
import com.temnenkov.actor.TelegramInboundActor
import com.temnenkov.actor.TelegramOutboundActor
import com.temnenkov.levent.LeventProperties
import com.temnenkov.leventactor.DeadActor
import com.temnenkov.leventactor.leventLoop
import com.temnenkov.leventbus.LeventMessage
import com.temnenkov.leventbus.XodusLeventBus
import com.temnenkov.leventbus.createEnvironment
import com.temnenkov.telegram.TelegramBot
import com.temnenkov.utils.enrichSystemProperties
import com.temnenkov.utils.push
import mu.KotlinLogging
import java.time.Duration

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        logger.error("config not found")
        return
    }

    enrichSystemProperties(args[0])

    val (environment, loopStep) = createEnvironment()
    val dbFile = System.getProperty(LeventProperties.LB_DATABASE)
    logger.info { "environment created: dbfile  $dbFile, loopStep $loopStep" }

    val bus = XodusLeventBus(environment)

    val telegramBot = TelegramBot()

    leventLoop(
        3,
        "bus-",
        bus,
        mapOf(
            ActorAddress.TELEGRAM_INBOUND to TelegramInboundActor(telegramBot, bus),
            ActorAddress.GAMEFACADE to GameFacadeActor(),
            ActorAddress.TELEGRAM_OUTBOUND to TelegramOutboundActor(telegramBot),
            ActorAddress.ADAPTER_GAMEFACADE_TELEGRAM to AdapterGameFacadeTelegramActor(),
            ActorAddress.ADAPTER_TELEGRAM_GAMEFACADE to AdapterTelegramGameFacadeActor()
        ),
        environment,
        loopStep,
        DeadActor()
    )

    logger.info { "event loop started" }

    environment.push(
        LeventMessage(
            to = ActorAddress.TELEGRAM_INBOUND,
            maxDuration = Duration.ofSeconds(TelegramBot.longPollingTimeout() + 10)
        )
    )
}

private val logger = KotlinLogging.logger {}
