package com.mx.keyvalue.delegate

import com.mx.keyvalue.MXKeyValue

open class MXLongDelegate(kv: MXKeyValue, name: String, default: Long = 0) :
    MXBaseDelegate<Long>(kv, name, default) {
    override fun stringToObject(value: String): Long {
        return value.toLongOrNull() ?: default
    }

    override fun objectToString(obj: Long): String? {
        return obj.toString()
    }
}