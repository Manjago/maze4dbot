package com.temnenkov.leventbus

import jetbrains.exodus.ArrayByteIterable
import jetbrains.exodus.ByteIterable
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
    appendProperties: Properties? = null
) : LeventBus {

    private val properties: Properties
    init {
        properties = Properties(System.getProperties())
        appendProperties?.forEach { properties.put(it.key, it.value) }
    }

    private val env: Environment = newInstance(properties.getProperty(LB_DATABASE, "~/.leventbusData"))

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
                val key = cursor.key.toInstant()
                if (key > from) {
                    logger.trace { "too early call, now $from, queue due $key" }
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
                    LongBinding.longToEntry(key.plusMillis(message.maxDuration.toMillis()).toEpochMilli()),
                    StringBinding.stringToEntry(message.id)
                )

                return@computeInTransaction message
            }
            null
        }
    }

    override fun done(messageId: String) = env.executeInTransaction { txn ->
        val queueStore = openQueueStore(txn)
        queueStore.delete(txn, StringBinding.stringToEntry(messageId))
    }

    fun dumpIndexToList(): List<Pair<Instant, String>> = env.computeInTransaction { txn ->

        val result = mutableListOf<Pair<Instant, String>>()
        val indexStore = openIndexStore(txn)

        indexStore.openCursor(txn).use { cursor ->
            while (cursor.next) {
                val key = cursor.key.toInstant()
                val value = StringBinding.entryToString(cursor.value)
                result.add(key to value)
            }
        }

        result
    }

    fun dumpQueueToList(): List<Pair<String, LeventMessage>> = env.computeInTransaction { txn ->

        val result = mutableListOf<Pair<String, LeventMessage>>()
        val indexStore = openQueueStore(txn)

        indexStore.openCursor(txn).use { cursor ->
            while (cursor.next) {
                val key = StringBinding.entryToString(cursor.key)
                val value = cursor.value.bytesUnsafe.toLeventMessage()
                result.add(key to value)
            }
        }

        result
    }

    private fun ByteIterable.toInstant() = Instant.ofEpochMilli(LongBinding.entryToLong(this))

    private fun indexStoreName() = properties.getProperty(LB_INDEX_NAME, "LeventbusStoreMainIndex")

    private fun queueStoreName() = properties.getProperty(LB_STORE_NAME, "LeventbusStore")

    private fun openIndexStore(txn: Transaction) = env.openStore(
        indexStoreName(),
        StoreConfig.WITH_DUPLICATES,
        txn
    )

    private fun openQueueStore(txn: Transaction) =
        env.openStore(queueStoreName(), StoreConfig.WITHOUT_DUPLICATES, txn)

    companion object {
        private val logger = KotlinLogging.logger { }

        const val LB_DATABASE = "leventbus.database"
        const val LB_INDEX_NAME = "queueMainIndex"
        const val LB_STORE_NAME = "queueMainStore"
    }
}
