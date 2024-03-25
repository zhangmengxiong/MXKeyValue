package com.mx.example.app

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mx.example.databinding.ActivityKeyFilterTestBinding
import com.mx.example.utils.SPUtils
import com.mx.keyvalue.utils.MXPosition

class KeyFilterTestActivity : AppCompatActivity() {
    private val binding by lazy { ActivityKeyFilterTestBinding.inflate(layoutInflater) }
    private val key_pre = "KEY_PRE_TEST"
    private val key_position = MXPosition.ANY
    private val key_name = when (key_position) {
        MXPosition.START -> "${key_pre}_aaa"
        MXPosition.END -> "aaa_${key_pre}"
        MXPosition.ANY -> "bbb_${key_pre}_aaa"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.setExpireTxv.setOnClickListener {
            SPUtils.set(key_name, "1分钟失效:" + System.currentTimeMillis())
            Toast.makeText(this, "已设置$key_name", Toast.LENGTH_SHORT).show()
        }
        binding.deleteTxv.setOnClickListener {
            SPUtils.deleteFilter(key_pre, key_position)
            Toast.makeText(this, SPUtils.get(key_name, "失效"), Toast.LENGTH_SHORT).show()
        }
        binding.readExpireTxv.setOnClickListener {
            Toast.makeText(this, SPUtils.get(key_name, "失效"), Toast.LENGTH_SHORT).show()
        }
    }
}
