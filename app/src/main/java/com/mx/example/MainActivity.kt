package com.mx.example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mx.keyvalue.MXKeyValue
import com.mx.keyvalue.delegate.MXStringDelegate
import com.mx.keyvalue.secret.MXDESSecret
import java.lang.StringBuilder
import kotlin.concurrent.thread
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mxKeyValue = MXKeyValue(application, "mx_kv_test", MXDESSecret("uyrii809"))

        var spend = 0L
        val time = 2000
        repeat(time) {
            val key = generalString(12)
            val value = generalString(1280)
            val start = System.currentTimeMillis()
            mxKeyValue.set(key, value)
            val read_value = mxKeyValue.get(key)
            if (read_value != value) {
                println("错误：$key -> $value -> $read_value")
            } else {
//                    println("正确：$key -> $value")
            }
            spend += (System.currentTimeMillis() - start)
        }
        println("平均耗时：${spend / time.toFloat()} ms")
    }

    private fun generalString(size: Int): String {
        val KEYS = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        val stringBuilder = StringBuilder()
        repeat(size) {
            stringBuilder.append(KEYS.random())
        }
        return stringBuilder.toString()
    }
}