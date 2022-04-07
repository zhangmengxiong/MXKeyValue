package com.mx.keyvalue.secret

interface IMXSecret {
    /**
     * 生成随机密码
     */
    fun generalSecret(): String

    /**
     * 对Key字符串加密
     */
    fun encryptKey(key: String, secret: String): String

    /**
     * 对Key字符串解密
     */
    fun decryptKey(secretKey: String, secret: String): String

    /**
     * 对Value加密
     * @param key 对应的Key
     */
    fun encryptValue(key: String, value: String, secret: String): String

    /**
     * 对Value解密
     * @param key 对应的Key
     */
    fun decryptValue(key: String, secretValue: String, secret: String): String
}