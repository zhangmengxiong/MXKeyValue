package com.mx.keyvalue.delegate

import com.mx.keyvalue.MXKeyValue

open class MXDoubleDelegate(kv: MXKeyValue, name: String, private val default: Double = 0.0) :
    MXBaseDelegate<Double>(kv, Double::class.java, name) {
    override fun stringToObject(value: String?, clazz: Class<Double>): Double {
        return value?.toDoubleOrNull() ?: default
    }

    override fun objectToString(value: Double?, clazz: Class<Double>): String? {
        return value?.toString()
    }

}