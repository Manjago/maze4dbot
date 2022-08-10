package com.temnenkov.leventactor

import com.temnenkov.db.QueueDb
import com.temnenkov.db.StoreDb
import com.temnenkov.leventbus.LeventMessage
import java.time.Instant

interface LeventActor {
    fun handleMessage(leventMessage: LeventMessage, storeDb: StoreDb, queueDb: QueueDb): List<Pair<LeventMessage, Instant>>
}
