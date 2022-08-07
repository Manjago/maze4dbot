package com.temnenkov.leventbus

import com.temnenkov.utils.openIndexStore
import com.temnenkov.utils.openQueueStore
import com.temnenkov.utils.toInstant
import jetbrains.exodus.bindings.LongBinding
import jetbrains.exodus.bindings.StringBinding
import jetbrains.exodus.env.Environment
import mu.KotlinLogging
import java.time.Instant

class XodusLeventBus(
    private val env: Environment
) : LeventBus {

    override fun pull(from: Instant): LeventMessage? = env.computeInTransaction { txn ->

        val indexStore = env.openIndexStore(txn)
        val queueStore = env.openQueueStore(txn)

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

    fun env(): Environment = env

    fun dumpIndexToList(): List<Pair<Instant, String>> = env.computeInTransaction { txn ->

        val result = mutableListOf<Pair<Instant, String>>()
        val indexStore = env.openIndexStore(txn)

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
        val indexStore = env.openQueueStore(txn)

        indexStore.openCursor(txn).use { cursor ->
            while (cursor.next) {
                val key = StringBinding.entryToString(cursor.key)
                val value = cursor.value.bytesUnsafe.toLeventMessage()
                result.add(key to value)
            }
        }

        result
    }

    companion object {
        private val logger = KotlinLogging.logger { }
    }
}
