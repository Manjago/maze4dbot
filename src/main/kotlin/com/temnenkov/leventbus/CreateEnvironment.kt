package com.temnenkov.leventbus

import com.temnenkov.levent.LeventProperties
import jetbrains.exodus.env.Environment
import jetbrains.exodus.env.Environments
import java.util.Properties

fun createEnvironment(appendProperties: Properties? = null): Pair<Environment, Long> {
    val properties = Properties(System.getProperties())

    appendProperties?.forEach { properties[it.key] = it.value }

    val loopStep = properties.getProperty(LeventProperties.LB_LOOP_STEP, "100").toLong()

    return Environments.newInstance(properties.getProperty(LeventProperties.LB_DATABASE, "~/.leventbusData")) to loopStep
}
