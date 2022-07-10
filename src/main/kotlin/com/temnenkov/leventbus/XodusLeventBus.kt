package com.temnenkov.leventbus

import jetbrains.exodus.ArrayByteIterable
import jetbrains.exodus.bindings.LongBinding
import jetbrains.exodus.bindings.StringBinding
import jetbrains.exodus.env.Environment
import jetbrains.exodus.env.Environments.newInstance
import jetbrains.exodus.env.StoreConfig
import jetbrains.exodus.env.Transaction
import mu.KotlinLogging
import java.time.Instant
import java.util.Properties

class XodusLeventBus(
    private val properties: Properties
) : LeventBus {

    private val env: Environment = newInstance(properties.getProperty("database", "~/.leventbusData"))

    override fun push(message: LeventMessage, due: Instant) = env.executeInTransaction { txn ->

        openQueueStore(txn).put(
            txn,
            StringBinding.stringToEntry(message.id),
            ArrayByteIterable(message.toByteArray())
        )

        openIndexStore(txn).put(
            txn,
            LongBinding.longToEntry(due.toEpochMilli()),
            StringBinding.stringToEntry(message.id)
        )
    }

    override fun pull(from: Instant): LeventMessage? = env.computeInTransaction { txn ->

        val indexStore = openIndexStore(txn)
        val queueStore = openQueueStore(txn)

        indexStore.openCursor(txn).use { cursor ->

            while (cursor.next) {

                val key = cursor.key
                val keyInstant = Instant.ofEpochMilli(LongBinding.entryToLong(key))
                if (keyInstant > from) {
                    logger.trace { "too early call, now $from, queue due $keyInstant" }
                    break
                }

                val id = cursor.value
                cursor.deleteCurrent()

                val queueItem = queueStore.get(txn, id)
                if (queueItem == null) {
                    logger.trace { "queueItem ${StringBinding.entryToString(id)} already processed" }
                    continue
                }

                val message = queueItem.bytesUnsafe.toLeventMessage()

                indexStore.put(
                    txn,
                    LongBinding.longToEntry(keyInstant.plusMillis(message.maxDuration.toMillis()).toEpochMilli()),
                    StringBinding.stringToEntry(message.id)
                )

                return@computeInTransaction message
            }
            null
        }
    }

    fun dropAll() {
        env.executeInTransaction { txn ->
            env.removeStore(queueStoreName(), txn)
            env.removeStore(indexStoreName(), txn)
        }
    }

    private fun indexStoreName() = properties.getProperty("queueMainIndex", "LeventbusStoreMainIndex")

    private fun queueStoreName() = properties.getProperty("queue", "LeventbusStore")

    private fun openIndexStore(txn: Transaction) = env.openStore(
        indexStoreName(), StoreConfig.WITH_DUPLICATES, txn
    )

    private fun openQueueStore(txn: Transaction) =
        env.openStore(queueStoreName(), StoreConfig.WITHOUT_DUPLICATES, txn)

    companion object {
        private val logger = KotlinLogging.logger { }
    }
}
