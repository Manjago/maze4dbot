package com.temnenkov.leventactor

import com.temnenkov.levent.LeventProperties
import com.temnenkov.leventbus.LeventBus
import jetbrains.exodus.env.Environment
import jetbrains.exodus.env.Transaction
import mu.KotlinLogging
import java.util.Properties

class LeventLoopWorker(
    private val workerId: String,
    private val leventBus: LeventBus<Transaction, Environment>,
    private val actors: Map<String, LeventActor<Transaction, Environment>>,
    appendProperties: Properties? = null
) : Runnable {

    private val properties: Properties = Properties(System.getProperties())
    init {
        appendProperties?.forEach { properties.put(it.key, it.value) }
    }
    private val loopStep = properties.getProperty(LeventProperties.LB_LOOP_STEP, "100").toLong()

    override fun run() {
        while (!Thread.interrupted()) {
            try {
                val message = leventBus.pull()
                if (message == null) {
                    Thread.sleep(loopStep)
                    continue
                }

                actors[message.to]?.handleMessage(message, leventBus)
            } catch (ex: InterruptedException) {
                logger.warn(ex) { "Interrupted $workerId" }
                Thread.currentThread().interrupt()
            } catch (ex: Exception) {
                logger.error(ex) { "Exception happens on $workerId, ignored" }
            }
        }
    }

    companion object {
        private val logger = KotlinLogging.logger { }
    }
}
