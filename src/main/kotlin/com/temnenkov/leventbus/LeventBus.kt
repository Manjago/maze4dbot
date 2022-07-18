package com.temnenkov.leventbus

import java.time.Instant

interface LeventBus {
    fun pull(from: Instant = Instant.now()): LeventMessage?
}
