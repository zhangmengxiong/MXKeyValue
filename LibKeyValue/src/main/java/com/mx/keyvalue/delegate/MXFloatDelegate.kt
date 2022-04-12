package com.mx.keyvalue.delegate

import com.mx.keyvalue.MXKeyValue

open class MXFloatDelegate(kv: MXKeyValue, name: String, private val default: Float = 0f) :
    MXBaseDelegate<Float>(kv, Float::class.java, name) {
    override fun stringToObject(value: String?, clazz: Class<Float>): Float {
        return value?.toFloatOrNull() ?: default
    }

    override fun objectToString(value: Float?, clazz: Class<Float>): String? {
        return value?.toString()
    }
}