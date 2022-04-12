package com.mx.keyvalue.delegate

import com.mx.keyvalue.MXKeyValue
import kotlin.reflect.KProperty

abstract class MXBaseDelegate<T>(
    private val kv: MXKeyValue,
    private val name: String
) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T? {
        return stringToObject(kv.get(name))
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
        kv.set(name, objectToString(value))
    }

    abstract fun stringToObject(value: String?): T?
    abstract fun objectToString(value: T?): String?
}