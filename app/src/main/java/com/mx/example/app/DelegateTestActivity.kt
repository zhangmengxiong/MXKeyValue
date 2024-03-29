package com.mx.example.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.mx.example.databinding.ActivityDelegateTestBinding
import com.mx.example.utils.SPUtils
import com.mx.keyvalue.MXKeyValue
import com.mx.keyvalue.delegate.KVBaseDelegate
import com.mx.keyvalue.delegate.KVBoolDelegate
import com.mx.keyvalue.delegate.KVDoubleDelegate
import com.mx.keyvalue.delegate.KVFloatDelegate
import com.mx.keyvalue.delegate.KVIntDelegate
import com.mx.keyvalue.delegate.KVLongDelegate
import com.mx.keyvalue.delegate.KVStringDelegate

class DelegateTestActivity : AppCompatActivity() {

    private val binding by lazy { ActivityDelegateTestBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.testBtn.setOnClickListener {
            var boolDelegate by KVBoolDelegate(SPUtils.KV, "bool_test", true)
            println("测试 MXBoolDelegate -> $boolDelegate")
            boolDelegate = false
            println("测试 MXBoolDelegate -> $boolDelegate")


            var doubleDelegate by KVDoubleDelegate(SPUtils.KV, "double_test", 1.0)
            println("测试 MXDoubleDelegate -> $doubleDelegate")
            doubleDelegate = 2.0
            println("测试 MXDoubleDelegate -> $doubleDelegate")


            var floatDelegate by KVFloatDelegate(SPUtils.KV, "float_test", 1f)
            println("测试 MXFloatDelegate -> $floatDelegate")
            floatDelegate = 2f
            println("测试 MXFloatDelegate -> $floatDelegate")


            var intDelegate by KVIntDelegate(SPUtils.KV, "int_test", 1)
            println("测试 MXIntDelegate -> $intDelegate")
            intDelegate = 2
            println("测试 MXIntDelegate -> $intDelegate")


            var longDelegate by KVLongDelegate(SPUtils.KV, "long_test", 1)
            println("测试 MXLongDelegate -> $longDelegate")
            longDelegate = 2
            println("测试 MXLongDelegate -> $longDelegate")


            var stringDelegate by KVStringDelegate(SPUtils.KV, "string_test", "testdef")
            println("测试 MXStringDelegate -> $stringDelegate")
            stringDelegate = "2"
            println("测试 MXStringDelegate -> $stringDelegate")


            var beanDelegate by KVBeanDelegate(
                SPUtils.KV,
                TestBean::class.java,
                "bean_test",
                null
            )
            println("测试 BeanDelegate -> ${beanDelegate?.id} -> ${beanDelegate?.name}")
            beanDelegate = TestBean("2sdf", "name")
            println("测试 BeanDelegate -> ${beanDelegate?.id} -> ${beanDelegate?.name}")

        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class TestBean constructor(val id: String, val name: String)

    class KVBeanDelegate<T>(
        kv: MXKeyValue,
        private val clazz: Class<out T>,
        name: String,
        default: T
    ) : KVBaseDelegate<T>(kv, name, default) {
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
