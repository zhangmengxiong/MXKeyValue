package com.mx.keyvalue.delegate

import com.mx.keyvalue.MXKeyValue

open class KVStringDelegate(kv: MXKeyValue, key: String, default: String = "") :
    KVBaseDelegate<String>(kv, key, default) {
    override fun stringToObject(value: String): String {
        return value
    }

    override fun objectToString(obj: String): String? {
        return obj
    }
}