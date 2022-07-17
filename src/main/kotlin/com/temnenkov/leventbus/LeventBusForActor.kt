package com.temnenkov.leventbus

import java.time.Instant

interface LeventBusForActor<T, E> {
    fun push(message: LeventMessage, due: Instant = Instant.now(), doneMesssageId: String? = null, action: ((txn: T) -> Unit)? = null)
    fun done(messageId: String)
    fun env(): E
}
