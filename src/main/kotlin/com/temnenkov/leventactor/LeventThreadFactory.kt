package com.temnenkov.leventactor

import mu.KotlinLogging
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicLong

class LeventThreadFactory(private val threadPrefix: String) : ThreadFactory {

    private val backingThreadFactory = Executors.defaultThreadFactory()
    private val counter = AtomicLong(0)

    override fun newThread(r: Runnable): Thread {
        val newThread = backingThreadFactory.newThread(r)
        newThread.name = "$threadPrefix${counter.getAndIncrement()}"
        newThread.setUncaughtExceptionHandler { thread: Thread, throwable: Throwable ->
            logger.error(throwable) { "thread ${thread.name} threw exception ${throwable.message}" }
        }
        return newThread
    }

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}
