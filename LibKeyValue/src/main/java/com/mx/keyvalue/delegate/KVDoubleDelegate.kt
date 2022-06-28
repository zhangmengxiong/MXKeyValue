package com.mx.keyvalue.delegate

import com.mx.keyvalue.MXKeyValue

open class KVDoubleDelegate(kv: MXKeyValue, key: String, default: Double = 0.0) :
    KVBaseDelegate<Double>(kv, key, default) {
    override fun stringToObject(value: String): Double {
        return value.toDoubleOrNull() ?: default
    }

    override fun objectToString(obj: Double): String? {
        return obj.toString()
    }

}