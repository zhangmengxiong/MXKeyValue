package com.mx.keyvalue.crypt

import android.util.Base64
import com.mx.keyvalue.utils.KVUtils
import java.util.UUID
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.random.Random

/**
 * AES对称加密
 * @param key 加密用的Key
 */
open class KVAESCrypt(private val key: String) : IKVCrypt {
    companion object {
        private val salt_key = arrayOf(
            65, 110, 123, 56, 3, 115, 73, 29, 108, 117, 98, 47, 93, 10, 71,
            21, 32, 98, 15, 22, 95, 27, 67, 29, 114, 119, 73, 80, 29, 23, 118,
            2, 44, 35, 75, 59, 127, 44, 99, 64, 20, 100, 111, 115, 116, 124,
            59, 48, 87, 32, 124, 119, 17, 97, 29, 30, 115, 85, 31, 76, 70, 57, 21, 56,
            36, 18, 122, 24, 60, 125, 100, 15, 75, 86, 104, 54, 111, 113, 117,
            20, 56, 38, 34, 126, 119, 98, 13, 15, 15, 22, 67, 23, 18, 57, 11,
            55, 98, 1, 34, 124, 83, 17, 11, 116, 92, 59, 54, 110, 62, 64, 122,
            126, 124, 95, 58, 7, 70, 89, 119, 108, 31, 75, 47, 42, 114, 87, 34, 113
        )
        private val salt_iv = arrayOf(
            10, 113, 18, 62, 120, 101, 32, 35, 22, 17, 79, 54, 14, 107, 8, 6,
            86, 97, 24, 36, 25, 42, 105, 32, 24, 111, 91, 91, 123, 58, 53,
            18, 108, 103, 126, 24, 30, 100, 41, 30, 63, 49, 42, 114, 126,
            23, 31, 109, 53, 58, 113, 18, 89, 104, 127, 122, 45, 79, 105,
            52, 7, 93, 79, 106, 20, 16, 66, 39, 43, 34, 57, 102, 69, 42,
            100, 127, 9, 76, 126, 4, 70, 65, 58, 56, 99, 38, 127, 27, 97,
            28, 82, 30, 122, 69, 1, 61, 50, 123, 70, 101, 3, 7, 46, 83, 94,
            11, 3, 13, 37, 109, 124, 44, 78, 107, 32, 33, 87, 101, 55,
            4, 29, 37, 50, 84, 39, 109, 67, 2
        )
        private val symbols = arrayOf(
            '!', '@', '#', '$', '^', '&', '*', '(', ')', '_', '+', '-',
            '=', '~', '`', ';', ':', ',', '<', '.', '>', '/', '?'
        )
        private val source = ('a'..'z') + symbols + ('A'..'Z') + symbols + ('0'..'9') + symbols
    }

    private val transformation = "AES/CBC/PKCS5Padding"

    private fun generalMixKey(): ByteArray {
        val list = key.toMutableList()
        val keys = StringBuilder()
        for (i in salt_key) {
            if (list.size > 0) {
                keys.append(list.removeAt(0))
            }
            keys.append(source[i % source.size])
        }
        keys.append(list)
        return KVUtils.md5(keys.toString().toByteArray(), 16).toByteArray()
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
        keys.append(list)
        return KVUtils.md5(keys.toString().toByteArray(), 16).toByteArray()
    }

    private val keySpec = SecretKeySpec(generalMixKey(), "AES")
    private val ivSpec = IvParameterSpec(generalMixIv())
    private val encryptCipher: Cipher
        get() = Cipher.getInstance(transformation).apply {
            init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)
        }
    private val decryptCipher: Cipher
        get() = Cipher.getInstance(transformation).apply {
            init(Cipher.DECRYPT_MODE, keySpec, ivSpec)
        }

    override fun generalSalt(): String {
        val length = Random.nextInt(25, 30)
        return UUID.randomUUID().toString().replace("-", "").substring(0, length)
    }

    override fun encrypt(key: String, value: String, salt: String): String? {
        val relBytes = try {
            encryptCipher.doFinal((salt + value + key).toByteArray()) ?: return null
        } catch (e: Exception) {
            return null
        }
        val relBase = Base64.encode(relBytes, Base64.DEFAULT)
        return String(relBase)
    }

    override fun decrypt(key: String, secretValue: String, salt: String): String? {
        val bytes = try {
            decryptCipher.doFinal(Base64.decode(secretValue, Base64.DEFAULT)) ?: return null
        } catch (e: Exception) {
            return null
        }
        val relBase = String(bytes)
        return relBase.substring(salt.length, relBase.length - key.length)
    }
}