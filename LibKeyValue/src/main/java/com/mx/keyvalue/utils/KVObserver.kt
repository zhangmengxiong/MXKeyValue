package com.mx.keyvalue.utils

interface KVObserver {
    fun onChange(key: String, value: String?)
}