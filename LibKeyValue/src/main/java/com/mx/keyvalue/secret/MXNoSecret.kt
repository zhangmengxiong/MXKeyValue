package com.mx.keyvalue.secret

/**
 * 不加密
 */
open class MXNoSecret : IMXSecret {
    override fun generalSecret(): String {
        return ""
    }

    override fun encrypt(key: String, value: String?, salt: String): String? {
        return value
    }

    override fun decrypt(key: String, secretValue: String?, salt: String): String? {
        return secretValue
    }
}