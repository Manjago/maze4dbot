package com.temnenkov.leventbus

import java.time.Instant

interface LeventBus<T, E> : LeventBusForActor<T, E> {
    fun pull(from: Instant = Instant.now()): LeventMessage?
}
