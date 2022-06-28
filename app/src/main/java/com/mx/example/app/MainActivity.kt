package com.mx.example.app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mx.example.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        expireTestTxv.setOnClickListener {
            startActivity(Intent(this, ExpireTestActivity::class.java))
        }
        threadTestTxv.setOnClickListener {
            startActivity(Intent(this, MultThreadTestActivity::class.java))
        }
        delegateTestTxv.setOnClickListener {
            startActivity(Intent(this, DelegateTestActivity::class.java))
        }
    }
}
