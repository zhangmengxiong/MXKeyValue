package com.mx.keyvalue.secret

import android.util.Base64
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * DES对称加密
 */
open class MXDESSecret(private val desKeySpec: String) : IMXSecret {
    private val keySpec = SecretKeySpec(desKeySpec.toByteArray(), "AES")
    private val encryptCipher = Cipher.getInstance("AES").apply {
        init(Cipher.ENCRYPT_MODE, keySpec, IvParameterSpec(desKeySpec.toByteArray()))
    }
    private val decryptCipher = Cipher.getInstance("AES").apply {
        init(Cipher.DECRYPT_MODE, keySpec, IvParameterSpec(desKeySpec.toByteArray()))
    }

    override fun generalSecret(): String {
        return UUID.randomUUID().toString()
    }

    override fun encrypt(key: String, value: String?, salt: String): String? {
        value ?: return null
        val relBytes = encryptCipher.doFinal((salt + value + key).toByteArray())
        val relBase = Base64.encode(relBytes, Base64.DEFAULT)
        return String(relBase)
    }

    override fun decrypt(key: String, secretValue: String?, salt: String): String? {
        secretValue ?: return null
        val relBase = String(decryptCipher.doFinal(Base64.decode(secretValue, Base64.DEFAULT)))
        return relBase.substring(salt.length, relBase.length - key.length)
    }
}