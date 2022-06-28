package com.mx.example.utils

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.mx.example.MyApp
import com.mx.keyvalue.MXKeyValue
import com.mx.keyvalue.crypt.KVAESCrypt
import com.mx.keyvalue.delegate.KVBaseDelegate
import com.mx.keyvalue.store.sqlite.KVSqliteStore

// 缓存类
object SPUtils {
    val KV by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        MXKeyValue.Builder(MyApp.appContext, "kvdb_kv_v1")
            .setCrypt(KVAESCrypt("27e2125d0a11a9aa65b9c9773673bc2a"))
            .setStore(KVSqliteStore())
            .build()
    }

    fun get(key: String, def: String? = null): String? {
        return KV.get(key, def)
    }

    fun set(key: String, value: String?, expire_time: Long? = null): Boolean {
        return KV.set(key, value, expire_time)
    }

    fun cleanAll() {
        KV.cleanAll()
    }

    fun delete(key: String): Boolean {
        return KV.delete(key)
    }

    class KVBeanDelegate<T>(
        kv: MXKeyValue,
        private val clazz: Class<out T>,
        name: String,
        default: T?
    ) : KVBaseDelegate<T?>(kv, name, default) {
        override fun stringToObject(value: String): T? {
            try {
                val mapper = ObjectMapper()
                mapper.registerKotlinModule()
                return mapper.readValue(value, clazz)
            } catch (e: Exception) {
            }
            return default
        }

        override fun objectToString(obj: T?): String? {
            obj ?: return null
            try {
                val mapper = ObjectMapper()
                return mapper.writeValueAsString(obj)
            } catch (e: Exception) {
            }
            return null
        }
    }
}