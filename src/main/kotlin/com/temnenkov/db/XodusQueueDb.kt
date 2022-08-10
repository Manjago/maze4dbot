package com.temnenkov.db

import com.temnenkov.leventbus.LeventMessage
import com.temnenkov.leventbus.toLeventMessage
import com.temnenkov.utils.openIndexStore
import com.temnenkov.utils.openQueueStore
import com.temnenkov.utils.toEntry
import jetbrains.exodus.env.Environment
import jetbrains.exodus.env.Transaction
import mu.KotlinLogging
import java.time.Instant

class XodusQueueDb(
    private val env: Environment,
    private val txn: Transaction
) : QueueDb {

    override fun push(message: LeventMessage, due: Instant) {
        if (!env.openQueueStore(txn).put(
                txn,
                message.id.toEntry(),
                message.toEntry()
            )
        ) {
            logger.error { "fail put message $message with ${message.id}" }
        }

        if (!env.openIndexStore(txn).put(
                txn,
                due.toEntry(),
                message.id.toEntry()
            )
        ) {
            logger.error { "fail put index ${message.id} with due $due" }
        }
    }

    override fun getMessageFromQueue(id: String): LeventMessage? {
        val raw = env.openQueueStore(txn).get(txn, id.toEntry())
        return raw?.bytesUnsafe?.toLeventMessage()
    }

    override fun done(messageId: String): Boolean = env.openQueueStore(txn).delete(txn, messageId.toEntry())

    companion object {
        private val logger = KotlinLogging.logger { }
    }
}
