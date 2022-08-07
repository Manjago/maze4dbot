package com.temnenkov.db

import com.temnenkov.leventbus.LeventMessage
import com.temnenkov.utils.openIndexStore
import com.temnenkov.utils.openQueueStore
import com.temnenkov.utils.toEntry
import jetbrains.exodus.env.Environment
import jetbrains.exodus.env.Transaction
import java.time.Instant

class XodusQueueDb(
    private val env: Environment,
    private val txn: Transaction
) : QueueDb {

    override fun push(message: LeventMessage, due: Instant) {
        env.openQueueStore(txn).put(
            txn,
            message.id.toEntry(),
            message.toEntry()
        )

        env.openIndexStore(txn).put(
            txn,
            due.toEntry(),
            message.id.toEntry()
        )
    }

    override fun done(messageId: String): Boolean = env.openQueueStore(txn).delete(txn, messageId.toEntry())
}
