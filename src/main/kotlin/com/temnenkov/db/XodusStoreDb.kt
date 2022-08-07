package com.temnenkov.db

import com.temnenkov.utils.fromEntry
import com.temnenkov.utils.toEntry
import jetbrains.exodus.env.Environment
import jetbrains.exodus.env.Store
import jetbrains.exodus.env.StoreConfig
import jetbrains.exodus.env.Transaction

class XodusStoreDb(
    private val env: Environment,
    private val txn: Transaction
) : StoreDb {

    override fun put(collection: String, key: String, value: String): Boolean =
        store(collection).put(txn, key.toEntry(), value.toEntry())

    override fun get(collection: String, key: String): String? = store(collection).get(txn, key.toEntry())?.fromEntry()

    private fun store(collection: String): Store = env.openStore(collection, StoreConfig.WITHOUT_DUPLICATES, txn)
}
