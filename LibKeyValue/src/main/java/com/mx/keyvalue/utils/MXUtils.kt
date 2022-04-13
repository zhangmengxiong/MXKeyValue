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
        val hex = StringBuilder(hash.size * 2)
        for (b in hash) {
            var str = Integer.toHexString(b.toInt())
            if (b < 0x10) {
                str = "0$str"
            }
            hex.append(str.substring(str.length - 2))
        }
        return hex.toString().substring(0, size)
    }
}