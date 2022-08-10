package com.temnenkov.db

import com.temnenkov.leventbus.LeventMessage
import java.time.Instant

interface QueueDb {
    fun push(message: LeventMessage, due: Instant = Instant.now())
    fun getMessageFromQueue(id: String): LeventMessage?
    fun done(messageId: String): Boolean
}
