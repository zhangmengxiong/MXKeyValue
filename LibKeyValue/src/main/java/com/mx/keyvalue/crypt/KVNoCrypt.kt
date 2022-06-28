package com.mx.keyvalue.crypt

/**
 * 不加密
 */
open class KVNoCrypt : IKVCrypt {
    override fun generalSalt(): String {
        return ""
    }

    override fun encrypt(key: String, value: String, salt: String): String? {
        return value
    }

    override fun decrypt(key: String, secretValue: String, salt: String): String? {
        return secretValue
    }
}