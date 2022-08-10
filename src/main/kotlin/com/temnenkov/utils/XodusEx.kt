package com.temnenkov.utils

import com.temnenkov.db.XodusQueueDb
import com.temnenkov.db.XodusStoreDb
import com.temnenkov.leventbus.LeventMessage
import com.temnenkov.leventbus.toByteArray
import jetbrains.exodus.ArrayByteIterable
import jetbrains.exodus.ByteIterable
import jetbrains.exodus.bindings.LongBinding
import jetbrains.exodus.bindings.StringBinding
import jetbrains.exodus.env.Environment
import jetbrains.exodus.env.StoreConfig
import jetbrains.exodus.env.Transaction
import jetbrains.exodus.env.TransactionalExecutable
import mu.KotlinLogging
import java.time.Instant

fun String.toEntry(): ArrayByteIterable = StringBinding.stringToEntry(this)

fun ByteIterable.fromEntry(): String = StringBinding.entryToString(this)

fun LeventMessage.toEntry(): ByteIterable = ArrayByteIterable(this.toByteArray())

fun Instant.toEntry(): ArrayByteIterable = LongBinding.longToEntry(this.toEpochMilli())

fun ByteIterable.toInstant(): Instant = Instant.ofEpochMilli(LongBinding.entryToLong(this))

fun Environment.openIndexStore(txn: Transaction) = this.openStore(
    INDEX_STORE,
    StoreConfig.WITH_DUPLICATES,
    txn
)

fun Environment.openQueueStore(txn: Transaction) =
    this.openStore(QUEUE_STORE, StoreConfig.WITHOUT_DUPLICATES, txn)

fun Environment.push(message: LeventMessage) = this.executeInTransaction { txn ->
    XodusQueueDb(this, txn).push(message, Instant.now())
}

fun Environment.done(messageId: String) = this.executeInTransaction { txn ->
    XodusQueueDb(this, txn).done(messageId)
}

fun Environment.get(collection: String, key: String) = this.computeInTransaction { txn ->
    XodusStoreDb(this, txn).get(collection, key)
}

fun Environment.myExecuteInTransaction(executable: TransactionalExecutable): Boolean {
    return myExecuteInTransaction(executable, this.beginTransaction())
}

fun myExecuteInTransaction(
    executable: TransactionalExecutable,
    txn: Transaction
): Boolean {
    val result: Boolean
    try {
        var counter = 0
        while (true) {
            ++counter
            logger.info { "wanna exec $counter" }
            executable.execute(txn)
            logger.info { "exec done" }
            if (txn.isReadonly || // txn can be read-only if Environment is in read-only mode
                txn.isFinished || // txn can be finished if, e.g., it was aborted within executable
                txn.flush()
            ) {
                logger.info { "wanna break" }
                break
            }
            logger.info { "wanna revert" }
            txn.revert()
            logger.info { "reverted" }
        }
    } catch (e: Exception) {
        logger.error(e) { "exception happens ${e.message}" }
    } finally {
        logger.info { "enter finally" }
        if (!txn.isFinished) {
            logger.info { "wanna abort" }
            txn.abort()
            logger.info { "aborted" }
            result = false
        } else {
            logger.info { "result = true" }
            result = true
        }
        logger.info { "exit finally" }
    }
    return result
}

private val logger = KotlinLogging.logger { }
private const val INDEX_STORE = "___index___"
private const val QUEUE_STORE = "___queue___"
