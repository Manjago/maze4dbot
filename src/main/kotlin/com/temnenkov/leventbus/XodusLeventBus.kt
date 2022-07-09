package com.temnenkov.leventbus

import jetbrains.exodus.ArrayByteIterable
import jetbrains.exodus.bindings.LongBinding
import jetbrains.exodus.bindings.StringBinding
import jetbrains.exodus.env.Environment
import jetbrains.exodus.env.Environments.newInstance
import jetbrains.exodus.env.StoreConfig
import java.time.Instant
import java.util.Properties

class XodusLeventBus(private val properties: Properties) : LeventBus {

    private val env: Environment = newInstance(properties.getProperty("database", "~/.leventbusData"))

    override fun push(message: LeventMessage, due: Instant) = env.executeInTransaction { txn ->

        fun putItem() {
            val store =
                env.openStore(properties.getProperty("queue", "LeventbusStore"), StoreConfig.WITHOUT_DUPLICATES, txn)

            val key = StringBinding.stringToEntry(message.id)
            val value = ArrayByteIterable(message.toByteArray())

            store.put(txn, key, value)
        }

        fun putIndex() {
            val store =
                env.openStore(
                    properties.getProperty("queueMainIndex", "LeventbusStoreMainIndex"),
                    StoreConfig.WITH_DUPLICATES,
                    txn
                )

            val key = LongBinding.longToEntry(due.toEpochMilli())
            val value = StringBinding.stringToEntry(message.id)

            store.put(txn, key, value)
        }

        putItem()
        putIndex()
    }

    override fun pull(from: Instant): LeventMessage? {
        TODO("Not yet implemented")
    }
}
