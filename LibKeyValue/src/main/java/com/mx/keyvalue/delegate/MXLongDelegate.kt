package com.mx.keyvalue.delegate

import com.mx.keyvalue.MXKeyValue
import kotlin.reflect.KProperty

class MXLongDelegate(
    private val mxKeyValue: MXKeyValue,
    private val name: String,
    private val default: Long = 0
) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): Long {
        return mxKeyValue.get(name)?.toLongOrNull() ?: default
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Long?) {
        mxKeyValue.set(name, value?.toString())
    }
}