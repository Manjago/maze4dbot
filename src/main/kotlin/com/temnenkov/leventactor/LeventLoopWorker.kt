package com.temnenkov.leventactor

import com.temnenkov.db.XodusQueueDb
import com.temnenkov.db.XodusStoreDb
import com.temnenkov.leventbus.LeventBus
import jetbrains.exodus.env.Environment
import mu.KotlinLogging

class LeventLoopWorker(
    private val workerId: String,
    private val leventBus: LeventBus,
    private val actors: Map<String, LeventActor>,
    private val env: Environment,
    private val loopStep: Long
) : Runnable {

    override fun run() {
        while (!Thread.interrupted()) {
            try {
                val message = leventBus.pull()
                if (message != null) {
                    val actor = actors[message.to]

                    if (actor != null) {
                        env.executeInTransaction { txn ->
                            actor.handleMessage(
                                message,
                                XodusStoreDb(env, txn),
                                XodusQueueDb(env, txn)
                            )
                        }
                    }
                } else {
                    Thread.sleep(loopStep)
                }
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
