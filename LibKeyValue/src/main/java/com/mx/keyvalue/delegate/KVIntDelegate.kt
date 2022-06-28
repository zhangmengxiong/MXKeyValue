package com.mx.keyvalue.delegate

import com.mx.keyvalue.MXKeyValue

open class KVIntDelegate(kv: MXKeyValue, key: String, default: Int = 0) :
    KVBaseDelegate<Int>(kv, key, default) {
    override fun stringToObject(value: String): Int {
        return value.toIntOrNull() ?: default
    }

    override fun objectToString(obj: Int): String? {
        return obj.toString()
    }
}