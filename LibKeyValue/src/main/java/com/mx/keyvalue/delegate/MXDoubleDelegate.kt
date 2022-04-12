package com.mx.keyvalue.delegate

import com.mx.keyvalue.MXKeyValue

open class MXDoubleDelegate(kv: MXKeyValue, name: String, private val default: Double = 0.0) :
    MXBaseDelegate<Double>(kv, name) {
    override fun stringToObject(value: String?): Double {
        return value?.toDoubleOrNull() ?: default
    }

    override fun objectToString(value: Double?): String? {
        return value?.toString()
    }

}