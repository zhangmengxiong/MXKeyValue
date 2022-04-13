package com.mx.keyvalue.base

internal interface IMXKeyValue {
    fun get(key: String): String?
    fun set(key: String, value: String?, dead_time: Long?): Boolean

    fun getAll(): Map<String, String>

    fun cleanExpire(): Boolean
    fun cleanAll(): Boolean
}