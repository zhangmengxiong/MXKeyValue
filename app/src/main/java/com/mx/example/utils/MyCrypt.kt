package com.mx.example.utils

import com.mx.keyvalue.secret.IMXCrypt
import java.util.*

class MyCrypt : IMXCrypt {
    private val divider = "$$$$$$$$$$$$"
    override fun generalSalt(): String {
        return UUID.randomUUID().toString().replace("-", "")
    }

    override fun encrypt(key: String, value: String, salt: String): String? {
        return "$key$divider$value"
    }

    override fun decrypt(key: String, secretValue: String, salt: String): String? {
        return secretValue.split(divider).lastOrNull()
    }
}