package com.mx.keyvalue

interface IKeyValue {
    fun get(key: String): String?
    fun set(key: String, value: String?): Boolean
    fun cleanAll(): Boolean
}