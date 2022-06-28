package com.mx.keyvalue.delegate

import com.mx.keyvalue.MXKeyValue

open class KVLongDelegate(kv: MXKeyValue, key: String, default: Long = 0) :
    KVBaseDelegate<Long>(kv, key, default) {
    override fun stringToObject(value: String): Long {
        return value.toLongOrNull() ?: default
    }

    override fun objectToString(obj: Long): String? {
        return obj.toString()
    }
}