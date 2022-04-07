package com.mx.keyvalue.secret

import android.util.Base64
import java.lang.Exception
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.random.Random

/**
 * AES对称加密
 * @param key 加密用的Key 可以用26个字母和数字组成 此处使用AES-128-CBC加密模式，key需要为16位。
 * @param ivParameter 使用CBC模式，需要一个向量iv，可增加加密算法的强度 , 需要为16位
 */
open class MXAESSecret(
    private val key: String,
    private val ivParameter: String
) : IMXSecret {
    private var encryptCipher: Cipher? = null
    private var decryptCipher: Cipher? = null

    init {
        if (key.length != 16 && ivParameter.length != 16) {
            throw Exception("参数‘key’、‘ivParameter’的长度需要为16位")
        }

        val keySpec = SecretKeySpec(key.toByteArray(), "AES")
        encryptCipher = Cipher.getInstance("AES/CBC/PKCS5Padding").apply {
            init(Cipher.ENCRYPT_MODE, keySpec, IvParameterSpec(ivParameter.toByteArray()))
        }
        decryptCipher = Cipher.getInstance("AES/CBC/PKCS5Padding").apply {
            init(Cipher.DECRYPT_MODE, keySpec, IvParameterSpec(ivParameter.toByteArray()))
        }
    }
    
    override fun generalSecret(): String {
        val length = Random.nextInt(15, 30)
        return UUID.randomUUID().toString().substring(0, length)
    }

    override fun encrypt(key: String, value: String?, salt: String): String? {
        value ?: return null
        val relBytes = encryptCipher?.doFinal((salt + value + key).toByteArray()) ?: return null
        val relBase = Base64.encode(relBytes, Base64.DEFAULT)
        return String(relBase)
    }

    override fun decrypt(key: String, secretValue: String?, salt: String): String? {
        secretValue ?: return null
        val relBase = String(
            decryptCipher?.doFinal(Base64.decode(secretValue, Base64.DEFAULT)) ?: return null
        )
        return relBase.substring(salt.length, relBase.length - key.length)
    }
}