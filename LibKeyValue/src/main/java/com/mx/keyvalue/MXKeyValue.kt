package com.mx.keyvalue

import android.content.Context
import com.mx.keyvalue.secret.IMXCrypt
import com.mx.keyvalue.secret.MXNoCrypt
import com.mx.keyvalue.store.IKVStore
import com.mx.keyvalue.store.sqlite.MXSqliteStore
import com.mx.keyvalue.utils.MXKVObservable
import com.mx.keyvalue.utils.MXKVObserver
import com.mx.keyvalue.utils.MXUtils

class MXKeyValue(
    private val context: Context,
    private val name: String,
    private val crypt: IMXCrypt = MXNoCrypt()
) {
    companion object {
        fun setDebug(debug: Boolean) {
            MXUtils.setDebug(debug)
        }
    }

    private val observerSet = HashMap<String, MXKVObservable>()
    private val ikvStore: IKVStore by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        MXSqliteStore(context.applicationContext, name.trim(), crypt)
    }

    init {
        if (!crypt.validate()) {
            // 验证Secret 工具类解密正确性
            throw Exception("${crypt::class.java.simpleName}  --->  IMXSecret Class validate error.")
        }
    }

    /**
     * 从SharedPreferences拷贝数据
     */
    fun cloneFromSharedPreferences(name: String) {
        val sp = context.getSharedPreferences(name, Context.MODE_PRIVATE)
        for (entry in sp.all) {
            val value = entry.value?.toString()
            if (value != null) {
                ikvStore.set(entry.key, value, null)
            }
        }
    }

    /**
     * 设置KV
     */
    fun set(key: String, value: String?, expire_time: Long? = null): Boolean {
        val key_trim = key.trim()
        if (key_trim.isBlank()) return false
        val result = if (value != null && value.isNotEmpty()) {
            ikvStore.set(key_trim, value, expire_time)
        } else {
            ikvStore.delete(key_trim)
        }
        observerSet[key]?.set(value)
        return result
    }

    /**
     * 获取KV
     */
    fun get(key: String, default: String? = null): String? {
        val key_trim = key.trim()
        if (key_trim.isBlank()) return default
        return ikvStore.get(key_trim) ?: default
    }

    /**
     * 删除Key
     */
    fun delete(key: String): Boolean {
        val key_trim = key.trim()
        if (key_trim.isBlank()) return false
        val result = ikvStore.delete(key_trim)
        observerSet[key]?.set(null)
        return result
    }

    fun getAll(): Map<String, String> {
        return ikvStore.getAll()
    }

    /**
     * 清理过期KV
     */
    fun cleanExpire() {
        ikvStore.cleanExpire()
    }

    /**
     * 清理数据
     */
    fun cleanAll(): Boolean {
        return ikvStore.cleanAll()
    }

    fun addKeyObserver(key: String, observer: MXKVObserver) {
        var observable = observerSet[key]
        if (observable == null) {
            observable = MXKVObservable(key, get(key))
            observerSet[key] = observable
        }
        observable.addObserver(observer)
    }

    fun removeKeyObserver(key: String, observer: MXKVObserver) {
        val observable = observerSet[key] ?: return
        observable.deleteObserver(observer)
    }

    fun release() {
        observerSet.clear()
        ikvStore.release()
    }
}