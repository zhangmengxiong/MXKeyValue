package com.mx.keyvalue.delegate

import com.mx.keyvalue.MXKeyValue

open class MXStringDelegate(kv: MXKeyValue, name: String, private val default: String = "") :
    MXBaseDelegate<String>(kv, name) {

    override fun stringToObject(value: String?): String? {
        return value ?: default
    }

    override fun objectToString(value: String?): String? {
        return value
    }
}