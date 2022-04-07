package com.mx.keyvalue.delegate

import com.mx.keyvalue.MXKeyValue
import kotlin.reflect.KProperty

class MXFloatDelegate(
    private val mxKeyValue: MXKeyValue,
    private val name: String,
    private val default: Float = 0f
) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): Float {
        return mxKeyValue.get(name)?.toFloatOrNull() ?: default
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Float) {
        mxKeyValue.set(name, value.toString())
    }
}