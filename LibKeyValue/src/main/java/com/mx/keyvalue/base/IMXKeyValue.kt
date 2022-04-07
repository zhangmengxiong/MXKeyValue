package com.mx.keyvalue.base

internal interface IMXKeyValue {
    fun get(key: String): String?
    fun set(key: String, value: String?): Boolean
    fun cleanAll(): Boolean
}