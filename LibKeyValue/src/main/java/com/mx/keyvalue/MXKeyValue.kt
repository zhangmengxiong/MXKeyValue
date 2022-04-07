package com.mx.keyvalue

import android.content.Context
import com.mx.keyvalue.db.MXDBKeyValue
import com.mx.keyvalue.secret.IMXSecret
import com.mx.keyvalue.secret.MXNoSecret

class MXKeyValue(context: Context, name: String, secret: IMXSecret = MXNoSecret()) {
    private val dbKeyValue = MXDBKeyValue(context.applicationContext, name.trim(), secret)

    fun set(key: String, value: String?): Boolean {
        return dbKeyValue.set(key.trim(), value?.trim())
    }

    fun get(key: String, default: String? = null): String? {
        return dbKeyValue.get(key) ?: default
    }

    fun cleanAll(): Boolean {
        return dbKeyValue.cleanAll()
    }
}