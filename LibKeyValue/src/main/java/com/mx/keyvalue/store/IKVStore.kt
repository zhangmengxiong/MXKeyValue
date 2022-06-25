package com.mx.keyvalue.store

internal interface IKVStore {
    fun get(key: String): String?
    fun set(key: String, value: String, dead_time: Long?): Boolean
    fun delete(key: String): Boolean

    fun getAll(): Map<String, String>

    fun cleanExpire(): Boolean
    fun cleanAll(): Boolean

    fun release()
}