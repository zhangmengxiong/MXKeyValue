package com.mx.keyvalue.delegate

import com.mx.keyvalue.MXKeyValue

open class KVFloatDelegate(kv: MXKeyValue, key: String, default: Float = 0f) :
    KVBaseDelegate<Float>(kv, key, default) {
    override fun stringToObject(value: String): Float {
        return value.toFloatOrNull() ?: default
    }

    override fun objectToString(obj: Float): String? {
        return obj.toString()
    }
}