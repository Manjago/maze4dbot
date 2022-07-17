package com.temnenkov.leventactor

import com.temnenkov.leventbus.LeventBus
import java.util.Properties
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

fun <T, E> leventLoop(
    threadsCount: Int,
    threadPrefix: String,
    leventBus: LeventBus<T, E>,
    actors: Map<String, LeventActor<T, E>>,
    appendPropeties: Properties? = null
) {
    val executor: ExecutorService = Executors.newFixedThreadPool(threadsCount, LeventThreadFactory(threadPrefix))

    for (i in 0 until threadsCount) {
        executor.submit(LeventLoopWorker("worker $i", leventBus, actors, appendPropeties))
    }
}
