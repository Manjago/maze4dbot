package com.temnenkov.db

import com.temnenkov.leventbus.LeventMessage
import java.time.Instant

interface QueueDb {
    fun push(message: LeventMessage, due: Instant = Instant.now())
    fun done(messageId: String): Boolean
}
