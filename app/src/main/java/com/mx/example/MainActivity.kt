package com.mx.example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mx.keyvalue.MXKeyValue
import com.mx.keyvalue.secret.MXAESSecret
import java.lang.StringBuilder

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val KV = MXKeyValue(
            application, "mx_kv_test",
            MXAESSecret("34987xckj1230sdj", "821321235z3xcsdd")
        )
        KV.cleanAll()

        var spend = 0L
        val time = 20
        repeat(time) {
            val key = generalString(12)
            val value = generalString(1280)
            val start = System.currentTimeMillis()
            KV.set(key, value)
            val read_value = KV.get(key)
            if (read_value != value) {
                println("错误：$key -> $value -> $read_value")
            } else {
//                    println("正确：$key -> $value")
            }
            spend += (System.currentTimeMillis() - start)
        }
        println("平均耗时：${spend / time.toFloat()} ms")

        println(KV.getAll().entries.joinToString(",") { "${it.key}, ${it.value}" })
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