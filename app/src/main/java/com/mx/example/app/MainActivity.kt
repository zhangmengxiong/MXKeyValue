package com.mx.example.app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.mx.example.R
import com.mx.example.utils.SPUtils
import com.mx.keyvalue.MXKeyValue
import com.mx.keyvalue.delegate.*
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.StringBuilder
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread
import kotlin.random.Random

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
