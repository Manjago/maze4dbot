package com.temnenkov

import com.temnenkov.levent.LeventProperties
import com.temnenkov.leventactor.leventLoop
import com.temnenkov.leventbus.XodusLeventBus
import com.temnenkov.leventbus.createEnvironment
import mu.KotlinLogging
import java.io.FileInputStream
import java.util.Properties

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        logger.error("config not found")
        return
    }

    val appProps = Properties()
    appProps.load(FileInputStream(args[0]))
    val systemProperties = System.getProperties()
    appProps.forEach {
        systemProperties.setProperty(it.key.toString(), it.value.toString())
    }

    val (environment, loopStep) = createEnvironment()
    val dbFile = systemProperties.getProperty(LeventProperties.LB_DATABASE)
    logger.info { "environment created: dbfile  $dbFile, loopStep $loopStep" }

    val bus = XodusLeventBus(environment)

    leventLoop(
        3,
        "bus-",
        bus,
        mapOf(),
        environment,
        loopStep
    )

    logger.info { "event loop started" }
}

private val logger = KotlinLogging.logger {}
