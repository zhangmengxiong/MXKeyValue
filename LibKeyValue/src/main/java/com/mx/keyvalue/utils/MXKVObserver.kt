package com.mx.keyvalue.utils

interface MXKVObserver {
    fun onChange(key: String, value: String?)
}