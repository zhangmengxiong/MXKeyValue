package com.mx.example.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mx.example.databinding.ActivityMultThreadTestBinding
import com.mx.example.utils.SPUtils
import kotlin.concurrent.thread
import kotlin.random.Random

class MultThreadTestActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMultThreadTestBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.syncTestTxv.setOnClickListener {
            repeat(5) { index ->
                thread {
                    repeat(100000) { count ->
                        getSetUnit(index, count)
                        if (count > 0 && count % 10 == 0) {
                            println("次数：$count")
                        }
                    }
                }
            }
        }
    }

    private fun getSetUnit(index: Int, count: Int) {
        val key = "key_index_" + index + "_" + count + "_" + Random.nextLong()
        val value = "value_index_" + index + "_" + count + "_" + Random.nextLong()
        if (!SPUtils.set(key, value)) {
            println("写入错误：$key -> $value")
        }
        Thread.sleep(Random.nextLong(0, 20))
        val read = SPUtils.get(key)
        if (read != value) {
            println("读取错误1：$key -> $value")
            println("读取错误2：$key -> $read")
        }
    }

}
