package com.mx.keyvalue.delegate

import com.mx.keyvalue.MXKeyValue

open class KVBoolDelegate(kv: MXKeyValue, key: String, default: Boolean = false) :
    KVBaseDelegate<Boolean>(kv, key, default) {
    override fun stringToObject(value: String): Boolean {
        return value.toBoolean()
    }

    override fun objectToString(obj: Boolean): String? {
        return obj.toString()
    }
}