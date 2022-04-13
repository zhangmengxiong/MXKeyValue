package com.mx.keyvalue.secret

import android.util.Base64
import com.mx.keyvalue.utils.MXUtils
import java.lang.StringBuilder
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

    private val transformation = "AES/CBC/PKCS5Padding"
    private val source = ('a'..'z') + ('A'..'Z') + ('0'..'9') + arrayOf(
        '!', '@', '#', '$', '^', '&', '*', '(', ')', '_', '+', '-',
        '=', '~', '`', ';', ':', ',', '<', '.', '>', '/', '?'
    )
    private val salt_key = arrayOf(
        46, 85, 38, 47, 86, 0, 15, 54, 57, 56, 29, 22, 44, 109, 78,
        51, 49, 39, 78, 12, 55, 28, 42, 2, 116, 71, 28, 93, 66, 102, 41, 63,
        84, 40, 6, 24, 38, 77, 7, 84, 45, 22, 82, 94, 97, 14, 69, 30, 18,
        51, 57, 94, 118, 124, 86, 126, 119, 103, 114, 84, 103, 42, 105, 40
    )
    private val salt_iv = arrayOf(
        49, 75, 119, 8, 17, 68, 64, 12, 78, 106, 125, 109, 116, 11, 76,
        75, 41, 48, 21, 98, 56, 70, 75, 66, 39, 97, 109, 106, 89, 87,
        32, 111, 50, 100, 44, 12, 72, 37, 94, 40, 116, 31, 40, 6, 42, 49, 54,
        6, 44, 9, 26, 20, 42, 90, 54, 67, 70, 85, 35, 74, 17, 86, 111, 70
    )

    private var encryptCipher: Cipher? = null
    private var decryptCipher: Cipher? = null

    private fun generalMixKey(): ByteArray {
        val list = key.toMutableList()
        val keys = StringBuilder()
        for (i in salt_key) {
            if (list.size > 0) {
                keys.append(list.removeAt(0))
            }
            keys.append(source[i % source.size])
        }
        keys.append(list.toCharArray())
        return MXUtils.md5(keys.toString().toByteArray(), 16).toByteArray()
    }

    private fun generalMixIv(): ByteArray {
        val list = key.reversed().toMutableList()
        val keys = StringBuilder()
        for (i in salt_iv) {
            if (list.size > 0) {
                keys.append(list.removeAt(0))
            }
            keys.append(source[i % source.size])
        }
        keys.append(list.toCharArray())
        return MXUtils.md5(keys.toString().toByteArray(), 16).toByteArray()
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