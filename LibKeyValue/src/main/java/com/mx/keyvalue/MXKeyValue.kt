package com.mx.keyvalue

import android.content.Context
import com.mx.keyvalue.crypt.IKVCrypt
import com.mx.keyvalue.crypt.KVNoCrypt
import com.mx.keyvalue.store.IKVStore
import com.mx.keyvalue.store.sqlite.KVSqliteStore
import com.mx.keyvalue.utils.KVUtils
import com.mx.keyvalue.utils.KeyFilter
import com.mx.keyvalue.utils.MXPosition
import kotlin.concurrent.thread

class MXKeyValue private constructor(private val context: Context, private val store: IKVStore) {
    companion object {
        fun setDebug(debug: Boolean) {
            KVUtils.setDebug(debug)
        }
    }

    class Builder(private val context: Context, private val name: String) {
        private var crypt: IKVCrypt? = null
        private var store: IKVStore? = null

        /**
         * 加密类，默认=KVNoCrypt()
         */
        fun setCrypt(crypt: IKVCrypt): Builder {
            this.crypt = crypt
            return this
        }

        /**
         * 存储方式，默认=KVSqliteStore()
         */
        fun setStore(store: IKVStore): Builder {
            this.store = store
            return this
        }

        @Throws(exceptionClasses = [Exception::class])
        fun build(): MXKeyValue {
            val crypt = crypt ?: KVNoCrypt()
            if (!KVUtils.validate(crypt)) {// 验证crypt 工具类解密正确性
                throw Exception("${crypt::class.java.simpleName}  --->  IMXSecret Class validate error.")
            }

            val store = store ?: KVSqliteStore()
            store.create(context, name, crypt)

            return MXKeyValue(context, store)
        }
    }

    init {
        thread { store.cleanExpire() }
    }

    /**
     * 从SharedPreferences拷贝数据
     */
    fun cloneFromSharedPreferences(name: String) {
        val sp = context.getSharedPreferences(name, Context.MODE_PRIVATE)
        for (entry in sp.all) {
            val value = entry.value?.toString()
            if (value != null) {
                store.set(entry.key, value, null)
            }
        }
    }

    /**
     * 设置KV
     */
    fun set(key: String, value: String?, expire_time: Long? = null): Boolean {
        val key_trim = key.trim()
        if (key_trim.isBlank()) return false
        val result = if (!value.isNullOrEmpty()) {
            store.set(key_trim, value, expire_time)
        } else {
            store.delete(key_trim)
        }
        return result
    }

    /**
     * 获取KV
     */
    fun get(key: String, default: String? = null): String? {
        val key_trim = key.trim()
        if (key_trim.isBlank()) return default
        return store.get(key_trim) ?: default
    }

    /**
     * 删除Key
     */
    fun delete(key: String): Boolean {
        val key_trim = key.trim()
        if (key_trim.isBlank()) return false
        val result = store.delete(key_trim)
        return result
    }

    fun deleteFilter(key: String, position: MXPosition): Boolean {
        val key_trim = key.trim()
        if (key_trim.isBlank()) return false
        val result = store.deleteFilter(KeyFilter(key, position))
        return result
    }

    fun getAll(): Map<String, String> {
        return store.getAll()
    }

    /**
     * 清理过期KV
     */
    fun cleanExpire() {
        store.cleanExpire()
    }

    /**
     * 清理数据
     */
    fun cleanAll(): Boolean {
        return store.cleanAll()
    }

    fun release() {
        store.release()
    }
}