package com.mx.example.app

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mx.example.databinding.ActivityExpireTestBinding
import com.mx.example.utils.SPUtils
import java.util.concurrent.TimeUnit

class ExpireTestActivity : AppCompatActivity() {
    private val binding by lazy { ActivityExpireTestBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.setExpireTxv.setOnClickListener {
            SPUtils.set(
                "test_expire_key",
                "1分钟失效:" + System.currentTimeMillis(),
                System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(1)
            )
            Toast.makeText(this, SPUtils.get("test_expire_key", "失效"), Toast.LENGTH_SHORT).show()
        }
        binding.readExpireTxv.setOnClickListener {
            Toast.makeText(this, SPUtils.get("test_expire_key", "失效"), Toast.LENGTH_SHORT).show()
        }
        binding.deleteTxv.setOnClickListener {
            SPUtils.delete("test_expire_key")
            Toast.makeText(this, SPUtils.get("test_expire_key", "失效"), Toast.LENGTH_SHORT).show()
        }
    }
}
