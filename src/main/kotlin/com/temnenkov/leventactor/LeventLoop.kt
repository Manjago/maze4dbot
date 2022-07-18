package com.temnenkov.leventactor

import com.temnenkov.leventbus.LeventBus
import jetbrains.exodus.env.Environment
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

fun leventLoop(
    threadsCount: Int,
    threadPrefix: String,
    leventBus: LeventBus,
    actors: Map<String, LeventActor>,
    environment: Environment,
    loopStep: Long
) {
    val executor: ExecutorService = Executors.newFixedThreadPool(threadsCount, LeventThreadFactory(threadPrefix))

    for (i in 0 until threadsCount) {
        executor.submit(LeventLoopWorker("worker $i", leventBus, actors, environment, loopStep))
    }
}
