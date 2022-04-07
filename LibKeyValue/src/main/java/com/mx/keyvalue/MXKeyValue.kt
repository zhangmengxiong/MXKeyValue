package com.mx.keyvalue

import android.app.Application
import com.mx.keyvalue.secret.IMXSecret
import com.mx.keyvalue.secret.MXNoSecret

object MXKeyValue {
    private var application: Application? = null
    private var secret: IMXSecret? = null
    private var dbKeyValue: IKeyValue? = null

    fun init(application: Application, secret: IMXSecret = MXNoSecret()) {
        this.application = application
        this.secret = secret
        dbKeyValue = DBKeyValue(application, secret)
    }

    fun set(key: String, value: String) {
        dbKeyValue?.set(key, value)
    }

    fun get(key: String): String? {
        return dbKeyValue?.get(key)
    }
}