package com.mx.keyvalue.delegate

import com.mx.keyvalue.MXKeyValue

open class MXIntDelegate(kv: MXKeyValue, name: String, private val default: Int = 0) :
    MXBaseDelegate<Int>(kv, name) {
    override fun stringToObject(value: String?): Int {
        return value?.toIntOrNull() ?: default
    }

    override fun objectToString(value: Int?): String? {
        return value?.toString()
    }
}