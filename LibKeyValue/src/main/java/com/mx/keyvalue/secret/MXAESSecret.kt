package com.mx.keyvalue.secret

import android.util.Base64
import com.mx.keyvalue.utils.MXUtils
import java.security.MessageDigest
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.random.Random

/**
 * AES对称加密
 * @param key 加密用的Key
 */
open class MXAESSecret(private val key: String) : IMXSecret {
    companion object {
        const val transformation = "AES/CBC/PKCS5Padding"
    }

    private var encryptCipher: Cipher? = null
    private var decryptCipher: Cipher? = null

    private fun generalMixKey(): ByteArray {
        val a = if (key.isEmpty()) {
            "=-1238zxkjpo`1*/*-.z,xmjclkqazx.,mc,mjoi7093485=-()&*^%$"
        } else {// 加盐
            key + "mx_aes_key_salt_2-03887xcv@#%^^*!@!@#^*"
        }
        return MXUtils.md5(a.reversed()).reversed().toByteArray()
    }

    private fun generalMixIv(): ByteArray {
        val a = if (key.isEmpty()) {
            "=-asd8927)(&%^$^#_)(_)))%$%6891723kzxljcmn,mxcjqao.=-()&*^%$"
        } else {
            key + "mss24546x_aes_key_salt_2-03887xcv@#%^^*!@!@#^*"
        }
        return MXUtils.md5(a.reversed()).reversed().toByteArray()
    }

    init {
        val keySpec = SecretKeySpec(generalMixKey(), "AES")
        val ivSpec = IvParameterSpec(generalMixIv())
        encryptCipher = Cipher.getInstance(transformation).apply {
            init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)
        }
        decryptCipher = Cipher.getInstance(transformation).apply {
            init(Cipher.DECRYPT_MODE, keySpec, ivSpec)
        }
    }

    override fun generalSalt(): String {
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