package com.mx.keyvalue.delegate

import com.mx.keyvalue.MXKeyValue
import kotlin.reflect.KProperty

class MXStringDelegate(
    private val mxKeyValue: MXKeyValue,
    private val name: String
) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): String {
        return mxKeyValue.get(name) ?: ""
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String?) {
        mxKeyValue.set(name, value ?: "")
    }
}