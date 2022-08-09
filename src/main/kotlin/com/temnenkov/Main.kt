package com.temnenkov

import com.temnenkov.actor.ActorAddress
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

    leventLoop(
        3,
        "bus-",
        bus,
        mapOf(),
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
