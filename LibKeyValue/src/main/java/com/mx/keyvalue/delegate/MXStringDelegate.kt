package com.mx.keyvalue.delegate

import com.mx.keyvalue.MXKeyValue

open class MXStringDelegate(kv: MXKeyValue, name: String, default: String = "") :
    MXBaseDelegate<String>(kv, name, default) {
    override fun stringToObject(value: String): String {
        return value
    }

    override fun objectToString(obj: String): String? {
        return obj
    }
}