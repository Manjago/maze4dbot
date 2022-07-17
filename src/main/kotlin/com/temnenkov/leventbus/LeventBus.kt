package com.temnenkov.leventbus

import java.time.Instant

interface LeventBus {
    fun push(message: LeventMessage, due: Instant = Instant.now())
    fun pull(from: Instant = Instant.now()): LeventMessage?
    fun done(messageId: String)
}
