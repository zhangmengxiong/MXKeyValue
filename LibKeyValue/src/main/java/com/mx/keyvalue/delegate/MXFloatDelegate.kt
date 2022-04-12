package com.mx.keyvalue.delegate

import com.mx.keyvalue.MXKeyValue

open class MXFloatDelegate(kv: MXKeyValue, name: String, default: Float = 0f) :
    MXBaseDelegate<Float>(kv, name, default) {
    override fun stringToObject(value: String): Float {
        return value.toFloatOrNull() ?: default
    }

    override fun objectToString(obj: Float): String? {
        return obj.toString()
    }
}