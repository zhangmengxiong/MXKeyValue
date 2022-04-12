package com.mx.keyvalue.delegate

import com.mx.keyvalue.MXKeyValue

open class MXStringDelegate(kv: MXKeyValue, name: String, private val default: String = "") :
    MXBaseDelegate<String>(kv, String::class.java, name) {

    override fun stringToObject(value: String?, clazz: Class<String>): String? {
        return value ?: default
    }

    override fun objectToString(value: String?, clazz: Class<String>): String? {
        return value
    }
}