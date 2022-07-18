package com.temnenkov

import com.temnenkov.db.XodusQueueDb
import com.temnenkov.leventbus.LeventMessage
import com.temnenkov.leventbus.toByteArray
import jetbrains.exodus.ArrayByteIterable
import jetbrains.exodus.ByteIterable
import jetbrains.exodus.bindings.LongBinding
import jetbrains.exodus.bindings.StringBinding
import jetbrains.exodus.env.Environment
import jetbrains.exodus.env.StoreConfig
import jetbrains.exodus.env.Transaction
import java.time.Instant

fun String.toEntry() = StringBinding.stringToEntry(this)

fun ByteIterable.fromEntry() = StringBinding.entryToString(this)

fun LeventMessage.toEntry(): ByteIterable = ArrayByteIterable(this.toByteArray())

fun Instant.toEntry() = LongBinding.longToEntry(this.toEpochMilli())

fun ByteIterable.toInstant() = Instant.ofEpochMilli(LongBinding.entryToLong(this))

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

private const val INDEX_STORE = "___index___"
private const val QUEUE_STORE = "___queue___"
