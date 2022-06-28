package com.mx.keyvalue.utils

import android.os.Handler
import android.os.Looper

internal class KVObservable(val key: String, defaultValue: String?) {
    private val mHandler = Handler(Looper.getMainLooper())
    private val lock = Object()
    private val observerList = HashSet<KVObserver>()

    private var _value: String? = defaultValue

    fun set(value: String?) {
        if (value == _value) {
            return
        }
        _value = value
        val list = synchronized(lock) {
            observerList.toList()
        }
        if (list.isEmpty()) return
        if (Looper.myLooper() == Looper.getMainLooper()) {
            list.forEach { it.onChange(key, value) }
        } else {
            mHandler.post {
                list.forEach { it.onChange(key, value) }
            }
        }
    }

    fun get() = _value

    fun addObserver(o: KVObserver?) {
        o ?: return
        synchronized(lock) {
            observerList.add(o)
        }
        mHandler.post { o.onChange(key, _value) }
    }

    fun deleteObserver(o: KVObserver?) {
        o ?: return
        synchronized(lock) {
            observerList.remove(o)
        }
    }

    fun deleteObservers() {
        synchronized(lock) {
            mHandler.removeCallbacksAndMessages(null)
            observerList.clear()
        }
    }
}

