package com.mx.example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.mx.keyvalue.MXKeyValue
import com.mx.keyvalue.delegate.*
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


        var beanDelegate by MXBeanDelegate(KV, TestBean::class.java, "bean_test2")
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

    class MXBeanDelegate<T>(kv: MXKeyValue, clazz: Class<T>, name: String) :
        MXBaseDelegate<T>(kv, clazz, name) {
        override fun stringToObject(value: String?, clazz: Class<T>): T? {
            value ?: return null
            try {
                val mapper = ObjectMapper()
                mapper.registerKotlinModule()
                return mapper.readValue(value, clazz)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }

        override fun objectToString(value: T?, clazz: Class<T>): String? {
            value ?: return null
            try {
                val mapper = ObjectMapper()
                return mapper.writeValueAsString(value)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }
    }
}
