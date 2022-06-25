package com.mx.example.utils

import java.lang.StringBuilder
import java.security.MessageDigest
import java.util.*

object StringUtils {
    fun generalKey(): String {
        return (UUID.randomUUID().toString() + generalString(32)).md5()
    }

    fun generalString(size: Int): String {
        val KEYS = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        val stringBuilder = StringBuilder()
        repeat(size) {
            stringBuilder.append(KEYS.random())
        }
        return stringBuilder.toString()
    }

    private fun String.md5(): String {
        try {
            val instance = MessageDigest.getInstance("MD5")//获取md5加密对象
            val digest = instance.digest(this.toByteArray())//对字符串加密，返回字节数组
            val sb = StringBuffer()
            for (b in digest) {
                val i: Int = b.toInt() and 0xff//获取低八位有效值
                var hexString = Integer.toHexString(i)//将整数转化为16进制
                if (hexString.length < 2) {
                    hexString = "0$hexString"//如果是一位的话，补0
                }
                sb.append(hexString)
            }
            return sb.toString()

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }
}