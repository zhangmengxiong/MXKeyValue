package com.mx.example

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.mx.keyvalue.MXKeyValue
import com.mx.keyvalue.delegate.*
import com.mx.keyvalue.secret.MXAESSecret
import com.mx.keyvalue.secret.MXNoSecret
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.StringBuilder
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val KV = MXKeyValue(
            application, "mx_kv_test",
            MXAESSecret("89qew0lkcjz;lkui1=2=--093475kjhzcklj")
        )
        KV.cleanAll()

        setExpireTxv.setOnClickListener {
            KV.set(
                "test_expire_key",
                "1分钟失效:" + System.currentTimeMillis(),
                System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(1)
            )
            Toast.makeText(this, KV.get("test_expire_key", "失效"), Toast.LENGTH_SHORT).show()
        }
        readExpireTxv.setOnClickListener {
            Toast.makeText(this, KV.get("test_expire_key", "失效"), Toast.LENGTH_SHORT).show()
        }
        deleteTxv.setOnClickListener {
            KV.delete("test_expire_key")
            Toast.makeText(this, KV.get("test_expire_key", "失效"), Toast.LENGTH_SHORT).show()
        }

        var spend = 0L
        val time = 200
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

        var boolDelegate by MXBoolDelegate(KV, "bool_test", true)
        println("测试 MXBoolDelegate -> $boolDelegate")
        boolDelegate = false
        println("测试 MXBoolDelegate -> $boolDelegate")


        var doubleDelegate by MXDoubleDelegate(KV, "double_test", 1.0)
        println("测试 MXDoubleDelegate -> $doubleDelegate")
        doubleDelegate = 2.0
        println("测试 MXDoubleDelegate -> $doubleDelegate")


        var floatDelegate by MXFloatDelegate(KV, "float_test", 1f)
        println("测试 MXFloatDelegate -> $floatDelegate")
        floatDelegate = 2f
        println("测试 MXFloatDelegate -> $floatDelegate")


        var intDelegate by MXIntDelegate(KV, "int_test", 1)
        println("测试 MXIntDelegate -> $intDelegate")
        intDelegate = 2
        println("测试 MXIntDelegate -> $intDelegate")


        var longDelegate by MXLongDelegate(KV, "long_test", 1)
        println("测试 MXLongDelegate -> $longDelegate")
        longDelegate = 2
        println("测试 MXLongDelegate -> $longDelegate")


        var stringDelegate by MXStringDelegate(KV, "string_test", "testdef")
        println("测试 MXStringDelegate -> $stringDelegate")
        stringDelegate = "2"
        println("测试 MXStringDelegate -> $stringDelegate")


        var beanDelegate by MXBeanDelegate(KV, TestBean::class.java, "bean_test", null)
        println("测试 BeanDelegate -> ${beanDelegate?.id} -> ${beanDelegate?.name}")
        beanDelegate = TestBean("2sdf", "name")
        println("测试 BeanDelegate -> ${beanDelegate?.id} -> ${beanDelegate?.name}")

    }

    private fun generalString(size: Int): String {
        val KEYS = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        val stringBuilder = StringBuilder()
        repeat(size) {
            stringBuilder.append(KEYS.random())
        }
        return stringBuilder.toString()
    }


    @JsonIgnoreProperties(ignoreUnknown = true)
    data class TestBean constructor(val id: String, val name: String)

    class MXBeanDelegate<T>(
        kv: MXKeyValue,
        private val clazz: Class<out T>,
        name: String,
        default: T
    ) : MXBaseDelegate<T>(kv, name, default) {
        override fun stringToObject(value: String): T {
            try {
                val mapper = ObjectMapper()
                mapper.registerKotlinModule()
                return mapper.readValue(value, clazz)
            } catch (e: Exception) {
            }
            return default
        }

        override fun objectToString(obj: T): String? {
            obj ?: return null
            try {
                val mapper = ObjectMapper()
                return mapper.writeValueAsString(obj)
            } catch (e: Exception) {
            }
            return null
        }
    }
}
