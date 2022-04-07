package com.mx.keyvalue.secret

/**
 * 不加密
 */
open class MXNoSecret : IMXSecret {
    override fun generalSecret(): String {
        return ""
    }

    override fun encryptKey(key: String, secret: String): String {
        return key
    }

    override fun decryptKey(secretKey: String, secret: String): String {
        return secretKey
    }

    override fun encryptValue(key: String, value: String?, secret: String): String? {
        return value
    }

    override fun decryptValue(key: String, secretValue: String?, secret: String): String? {
        return secretValue
    }
}