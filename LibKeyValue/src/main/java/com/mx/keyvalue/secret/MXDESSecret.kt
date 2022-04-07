package com.mx.keyvalue.secret

import android.util.Base64
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * DES对称加密
 */
open class MXDESSecret(desKeySpec: String) : IMXSecret {
    private val keySpec = SecretKeySpec(desKeySpec.toByteArray(), "DES")
    private val encryptCipher = Cipher.getInstance("DES/CBC/PKCS5Padding").apply {
        init(Cipher.ENCRYPT_MODE, keySpec, IvParameterSpec(desKeySpec.toByteArray()))
    }
    private val decryptCipher = Cipher.getInstance("DES/CBC/PKCS5Padding").apply {
        init(Cipher.DECRYPT_MODE, keySpec, IvParameterSpec(desKeySpec.toByteArray()))
    }
    private val secretLength = 32

    override fun generalSecret(): String {
        return UUID.randomUUID().toString().replace("-", "")
    }

    override fun encryptKey(key: String, secret: String): String {
        val relBytes = encryptCipher.doFinal((key + secret).toByteArray())
        val relBase = Base64.encode(relBytes, Base64.DEFAULT)
        return String(relBase)
    }

    override fun decryptKey(secretKey: String, secret: String): String {
        val relBase =
            String(decryptCipher.doFinal(Base64.decode(secretKey.toByteArray(), Base64.DEFAULT)))
        val realKey = relBase.substring(0, relBase.length - secretLength)
        val realSecret = relBase.substring(relBase.length - secretLength, relBase.length)
        // 对secret校验
        return if (secret == realSecret) realKey else relBase
    }

    override fun encryptValue(key: String, value: String?, secret: String): String? {
        value ?: return null
        val relBytes = encryptCipher.doFinal((value + secret).toByteArray())
        val relBase = Base64.encode(relBytes, Base64.DEFAULT)
        return String(relBase)
    }

    override fun decryptValue(key: String, secretValue: String?, secret: String): String? {
        secretValue ?: return null
        val relBase =
            String(decryptCipher.doFinal(Base64.decode(secretValue, Base64.DEFAULT)))
        val realValue = relBase.substring(0, relBase.length - secretLength)
        val realSecret = relBase.substring(relBase.length - secretLength, relBase.length)
        // 对secret校验
        return if (secret == realSecret) realValue else relBase
    }
}