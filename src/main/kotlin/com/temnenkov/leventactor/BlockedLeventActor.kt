package com.temnenkov.leventactor

interface BlockedLeventActor : LeventActor {
    fun tryEntryLock(): Boolean
    fun releaseLock()
}
