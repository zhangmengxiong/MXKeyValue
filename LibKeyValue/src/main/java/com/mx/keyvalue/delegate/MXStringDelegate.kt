package com.mx.keyvalue.delegate

import com.mx.keyvalue.MXKeyValue
import kotlin.reflect.KProperty

open class MXStringDelegate(
    private val mxKeyValue: MXKeyValue,
    private val name: String,
    private val default: String = ""
) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): String {
        return mxKeyValue.get(name) ?: default
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        mxKeyValue.set(name, value)
    }
}