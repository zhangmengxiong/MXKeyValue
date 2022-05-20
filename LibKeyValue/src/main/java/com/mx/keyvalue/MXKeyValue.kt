package com.mx.keyvalue

import android.content.Context
import com.mx.keyvalue.base.IMXKeyValue
import com.mx.keyvalue.db.MXDBKeyValue
import com.mx.keyvalue.secret.IMXSecret
import com.mx.keyvalue.secret.MXNoSecret

class MXKeyValue(
    private val context: Context,
    name: String,
    secret: IMXSecret = MXNoSecret()
) {
    private val dbKeyValue: IMXKeyValue =
        MXDBKeyValue(context.applicationContext, name.trim(), secret)

    init {
        if (!secret.validate()) {
            // 验证Secret 工具类解密正确性
            throw Exception("${secret::class.java.simpleName}  --->  IMXSecret Class validate error.")
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
                dbKeyValue.set(entry.key, value, null)
            }
        }
    }

    /**
     * 设置KV
     */
    fun set(key: String, value: String?, expire_time: Long? = null): Boolean {
        val key_trim = key.trim()
        if (key_trim.isBlank()) return false
        return if (value != null && value.isNotEmpty()) {
            dbKeyValue.set(key_trim, value, expire_time)
        } else {
            dbKeyValue.delete(key_trim)
        }
    }

    /**
     * 获取KV
     */
    fun get(key: String, default: String? = null): String? {
        val key_trim = key.trim()
        if (key_trim.isBlank()) return default
        return dbKeyValue.get(key_trim) ?: default
    }

    /**
     * 删除Key
     */
    fun delete(key: String): Boolean {
        val key_trim = key.trim()
        if (key_trim.isBlank()) return false
        return dbKeyValue.delete(key_trim)
    }

    fun getAll(): Map<String, String> {
        return dbKeyValue.getAll()
    }

    /**
     * 清理过期KV
     */
    fun cleanExpire() {
        dbKeyValue.cleanExpire()
    }

    /**
     * 清理数据
     */
    fun cleanAll(): Boolean {
        return dbKeyValue.cleanAll()
    }
}