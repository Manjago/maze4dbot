package com.temnenkov.db

interface StoreDb {
    fun put(collection: String, key: String, value: String): Boolean
    fun get(collection: String, key: String): String?
}
