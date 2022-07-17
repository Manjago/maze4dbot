package com.temnenkov.leventbus

import java.time.Instant
import kotlin.test.assertEquals

fun XodusLeventBus.checkEmptyIndex() = with(dumpIndexToList()) {
    assertEquals(0, this.size)
}

fun XodusLeventBus.checkIndexElement(size: Int, index: Int, id: String, date: Instant) = with(this.dumpIndexToList()) {
    assertEquals(size, this.size)
    assertEquals(id, this[index].second)
    assertEquals(date, this[index].first)
}

fun XodusLeventBus.indexElementDate(size: Int, index: Int, id: String) = with(this.dumpIndexToList()) {
    assertEquals(size, this.size)
    assertEquals(id, this[index].second)
    this[index].first
}

fun XodusLeventBus.checkEmptyQueue() = with(dumpQueueToList()) {
    assertEquals(0, this.size)
}

fun XodusLeventBus.checkQueueElement(size: Int, index: Int, id: String, message: LeventMessage) = with(this.dumpQueueToList()) {
    assertEquals(size, this.size)
    assertEquals(id, this[index].first)
    assertEquals(message, this[index].second)
}

fun XodusLeventBus.queueElementMessage(size: Int, index: Int, id: String) = with(this.dumpQueueToList()) {
    assertEquals(size, this.size)
    assertEquals(id, this[index].first)
    this[index].first
}
