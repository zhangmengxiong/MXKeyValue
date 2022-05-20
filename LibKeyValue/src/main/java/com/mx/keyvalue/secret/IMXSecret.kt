package com.mx.keyvalue.secret

import com.mx.keyvalue.utils.MXUtils
import java.util.*

interface IMXSecret {
    /**
     * 生成随机密码
     */
    fun generalSalt(): String

    /**
     * 对Value加密
     * @param key 对应的Key
     */
    fun encrypt(key: String, value: String, salt: String): String?

    /**
     * 对Value解密
     * @param key 对应的Key
     */
    fun decrypt(key: String, secretValue: String, salt: String): String?

    /**
     * 验证当前加密类的正确性
     */
    fun validate(): Boolean {
        try {
            val key = UUID.randomUUID().toString().replace("-", "")
            val value = UUID.randomUUID().toString().replace("-", "") + System.currentTimeMillis()
            val salt = generalSalt()
            val secretKey = encrypt(key, value, salt)!!
            val desValue = decrypt(key, secretKey, salt)
            MXUtils.log("IMXSecret validate =>  $value  ---  $desValue")
            return value == desValue
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }
}