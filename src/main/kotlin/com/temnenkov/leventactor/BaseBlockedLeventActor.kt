package com.temnenkov.leventactor

import java.util.concurrent.atomic.AtomicBoolean

abstract class BaseBlockedLeventActor : BlockedLeventActor {

    private val locker = AtomicBoolean(false)

    override fun tryEntryLock(): Boolean = !locker.compareAndExchange(false, true)

    override fun releaseLock() = locker.set(false)
}
