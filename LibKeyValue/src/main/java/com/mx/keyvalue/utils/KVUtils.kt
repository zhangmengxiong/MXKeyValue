package com.mx.keyvalue.utils

import com.mx.keyvalue.crypt.IKVCrypt
import java.security.MessageDigest
import java.util.*

internal object KVUtils {
    private var debug = false
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

    fun validate(crypt: IKVCrypt): Boolean {
        try {
            val key = UUID.randomUUID().toString().replace("-", "")
            val value = UUID.randomUUID().toString().replace("-", "") + System.currentTimeMillis()
            val salt = crypt.generalSalt()
            val secretKey = crypt.encrypt(key, value, salt)!!
            val desValue = crypt.decrypt(key, secretKey, salt)
            log("IMXSecret validate =>  $value  ---  $desValue")
            return value == desValue
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }
}