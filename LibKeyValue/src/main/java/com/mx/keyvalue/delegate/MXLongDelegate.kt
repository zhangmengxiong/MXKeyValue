package com.mx.keyvalue.delegate

import com.mx.keyvalue.MXKeyValue

open class MXLongDelegate(kv: MXKeyValue, name: String, private val default: Long = 0) :
    MXBaseDelegate<Long>(kv, Long::class.java, name) {
    override fun stringToObject(value: String?, clazz: Class<Long>): Long {
        return value?.toLongOrNull() ?: default
    }

    override fun objectToString(value: Long?, clazz: Class<Long>): String? {
        return value?.toString()
    }
}