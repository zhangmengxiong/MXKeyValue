package com.mx.example

import android.app.Application
import com.mx.keyvalue.MXKeyValue

class MyApp : Application() {
    companion object {
        var _appContext: Application? = null
        val appContext: Application
            get() = _appContext!!
    }

    override fun onCreate() {
        super.onCreate()
        _appContext = this
        MXKeyValue.setDebug(true)
    }
}