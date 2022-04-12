package com.mx.keyvalue.delegate

import com.mx.keyvalue.MXKeyValue

open class MXLongDelegate(kv: MXKeyValue, name: String, private val default: Long = 0) :
    MXBaseDelegate<Long>(kv, name) {
    override fun stringToObject(value: String?): Long {
        return value?.toLongOrNull() ?: default
    }

    override fun objectToString(value: Long?): String? {
        return value?.toString()
    }
}