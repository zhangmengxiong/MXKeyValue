package com.mx.keyvalue.delegate

import com.mx.keyvalue.MXKeyValue

open class MXBoolDelegate(kv: MXKeyValue, name: String, private val default: Boolean = false) :
    MXBaseDelegate<Boolean>(kv, Boolean::class.java, name) {
    override fun stringToObject(value: String?, clazz: Class<Boolean>): Boolean {
        return value?.toBoolean() ?: default
    }

    override fun objectToString(value: Boolean?, clazz: Class<Boolean>): String? {
        return value?.toString()
    }
}