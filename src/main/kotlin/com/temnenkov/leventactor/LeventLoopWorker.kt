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
    private val loopStep: Long,
    private val deadActor: LeventActor
) : Runnable {

    override fun run() {
        while (!Thread.interrupted()) {
            try {
                val message = leventBus.pull()
                if (message != null) {
                    val actor = actors[message.to] ?: deadActor

                    env.executeInTransaction { txn ->
                        val queueDb = XodusQueueDb(env, txn)
                        val toSave = actor.handleMessage(
                            message,
                            XodusStoreDb(env, txn)
                        )
                        queueDb.done(message.id)
                        logger.info { "done $message" }
                        toSave?.forEach {
                            val (leventMessage, due) = it
                            queueDb.push(leventMessage, due)
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
