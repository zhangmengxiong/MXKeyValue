package com.mx.keyvalue.delegate

import com.mx.keyvalue.MXKeyValue

open class MXIntDelegate(kv: MXKeyValue, name: String, default: Int = 0) :
    MXBaseDelegate<Int>(kv, name, default) {
    override fun stringToObject(value: String): Int {
        return value.toIntOrNull() ?: default
    }

    override fun objectToString(obj: Int): String? {
        return obj.toString()
    }
}