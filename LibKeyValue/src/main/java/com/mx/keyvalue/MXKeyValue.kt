package com.mx.keyvalue

import android.content.Context
import com.mx.keyvalue.db.MXDBKeyValue
import com.mx.keyvalue.secret.IMXSecret
import com.mx.keyvalue.secret.MXNoSecret

class MXKeyValue(
    private val context: Context,
    name: String,
    secret: IMXSecret = MXNoSecret()
) {
    private val dbKeyValue = MXDBKeyValue(context.applicationContext, name.trim(), secret)

    /**
     * 从SharedPreferences拷贝数据
     */
    fun cloneFromSharedPreferences(name: String) {
        val sp = context.getSharedPreferences(name, Context.MODE_PRIVATE)
        for (entry in sp.all) {
            dbKeyValue.set(entry.key, entry.value?.toString())
        }
    }

    /**
     * 设置KV
     */
    fun set(key: String, value: String?): Boolean {
        return dbKeyValue.set(key.trim(), value?.trim())
    }

    /**
     * 获取KV
     */
    fun get(key: String, default: String? = null): String? {
        return dbKeyValue.get(key) ?: default
    }

    /**
     * 清理数据
     */
    fun cleanAll(): Boolean {
        return dbKeyValue.cleanAll()
    }
}