package com.mx.keyvalue.utils

import com.mx.keyvalue.BuildConfig
import java.security.MessageDigest

internal object MXUtils {
    private var debug = BuildConfig.DEBUG
    fun setDebug(debug: Boolean) {
        this.debug = debug
    }

    fun log(message: Any) {
        if (debug) {
            println("MXKV - $message")
        }
    }

    fun md5(content: ByteArray, size: Int): String {
        val hash = MessageDigest.getInstance("MD5").digest(content)
        val builder = StringBuilder()
        for (b in hash) {
            val str = Integer.toHexString(b.toInt() and 0xff)
            if (b < 0x10) {
                builder.append('0')
            }
            builder.append(str)
        }
        return builder.toString().substring(0, size).lowercase()
    }
}