package com.temnenkov.leventbus

import java.time.Instant

interface LeventBus {
    fun pull(from: Instant = Instant.now()): LeventMessage?
    fun dumpIndexToList(): List<Pair<Instant, String>>
    fun dumpQueueToList(): List<Pair<String, LeventMessage>>
}
