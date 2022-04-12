package com.mx.keyvalue.delegate

import com.mx.keyvalue.MXKeyValue

open class MXDoubleDelegate(kv: MXKeyValue, name: String, default: Double = 0.0) :
    MXBaseDelegate<Double>(kv, name, default) {
    override fun stringToObject(value: String): Double {
        return value.toDoubleOrNull() ?: default
    }

    override fun objectToString(obj: Double): String? {
        return obj.toString()
    }

}