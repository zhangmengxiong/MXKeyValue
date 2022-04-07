package com.mx.keyvalue.secret

interface IMXSecret {
    /**
     * 生成随机密码
     */
    fun generalSalt(): String

    /**
     * 对Value加密
     * @param key 对应的Key
     */
    fun encrypt(key: String, value: String?, salt: String): String?

    /**
     * 对Value解密
     * @param key 对应的Key
     */
    fun decrypt(key: String, secretValue: String?, salt: String): String?
}