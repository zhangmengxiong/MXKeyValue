package com.mx.keyvalue.delegate

import com.mx.keyvalue.MXKeyValue

open class MXBoolDelegate(kv: MXKeyValue, name: String, default: Boolean = false) :
    MXBaseDelegate<Boolean>(kv, name, default) {
    override fun stringToObject(value: String): Boolean {
        return value.toBoolean()
    }

    override fun objectToString(obj: Boolean): String? {
        return obj.toString()
    }
}