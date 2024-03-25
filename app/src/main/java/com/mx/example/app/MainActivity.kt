package com.mx.example.app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mx.example.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.expireTestTxv.setOnClickListener {
            startActivity(Intent(this, ExpireTestActivity::class.java))
        }
        binding.threadTestTxv.setOnClickListener {
            startActivity(Intent(this, MultThreadTestActivity::class.java))
        }
        binding.delegateTestTxv.setOnClickListener {
            startActivity(Intent(this, DelegateTestActivity::class.java))
        }
        binding.keyFilterTestTxv.setOnClickListener {
            startActivity(Intent(this, KeyFilterTestActivity::class.java))
        }
    }
}
