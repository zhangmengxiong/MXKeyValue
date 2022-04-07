package com.mx.keyvalue.delegate

import com.mx.keyvalue.MXKeyValue
import kotlin.reflect.KProperty

class MXBoolDelegate(
    private val mxKeyValue: MXKeyValue,
    private  val name: String,
    private val default: Boolean = false
) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): Boolean {
        return mxKeyValue.get(name)?.toBoolean() ?: default
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Boolean?) {
        mxKeyValue.set(name, value?.toString())
    }
}