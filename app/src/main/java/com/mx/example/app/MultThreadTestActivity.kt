package com.mx.example.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mx.example.R
import com.mx.example.utils.SPUtils
import com.mx.example.utils.StringUtils
import kotlinx.android.synthetic.main.activity_mult_thread_test.*
import kotlin.concurrent.thread
import kotlin.random.Random

class MultThreadTestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mult_thread_test)
        syncTestTxv.setOnClickListener {
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
        val key = "key_index_" + index + "_" + count
        val value = "value_index_" + index + "_" + count
        if (!SPUtils.set(key, value)) {
            println("写入错误：$key -> $value")
        }
        val read = SPUtils.get(key)
        if (read != value) {
            println("读取错误1：$key -> $value")
            println("读取错误2：$key -> $read")
        }
    }

}
