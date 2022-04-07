package com.mx.keyvalue.delegate

import com.mx.keyvalue.MXKeyValue
import kotlin.reflect.KProperty

class MXDoubleDelegate(
    private val mxKeyValue: MXKeyValue,
    private val name: String,
    private val default: Double = 0.0
) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): Double {
        return mxKeyValue.get(name)?.toDoubleOrNull() ?: default
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Double?) {
        mxKeyValue.set(name, value?.toString())
    }
}