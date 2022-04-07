package com.mx.keyvalue.delegate

import com.mx.keyvalue.MXKeyValue
import kotlin.reflect.KProperty

class MXIntDelegate(
    private val mxKeyValue: MXKeyValue,
    private val name: String,
    private val default: Int = 0
) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): Int {
        return mxKeyValue.get(name)?.toIntOrNull() ?: default
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Int) {
        mxKeyValue.set(name, value.toString())
    }
}