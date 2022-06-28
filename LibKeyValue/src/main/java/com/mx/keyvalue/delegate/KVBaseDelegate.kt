package com.mx.keyvalue.delegate

import com.mx.keyvalue.MXKeyValue
import kotlin.reflect.KProperty

abstract class KVBaseDelegate<T>(
    protected val kv: MXKeyValue,
    protected val key: String,
    protected val default: T
) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        val value = kv.get(key) ?: return default
        return stringToObject(value)
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        kv.set(key, objectToString(value))
    }

    abstract fun stringToObject(value: String): T
    abstract fun objectToString(obj: T): String?
}