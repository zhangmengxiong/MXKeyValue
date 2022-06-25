package com.mx.example.app

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mx.example.R
import com.mx.example.utils.SPUtils
import kotlinx.android.synthetic.main.activity_expire_test.*
import java.util.concurrent.TimeUnit

class ExpireTestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expire_test)

        setExpireTxv.setOnClickListener {
            SPUtils.set(
                "test_expire_key",
                "1分钟失效:" + System.currentTimeMillis(),
                System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(1)
            )
            Toast.makeText(this, SPUtils.get("test_expire_key", "失效"), Toast.LENGTH_SHORT).show()
        }
        readExpireTxv.setOnClickListener {
            Toast.makeText(this, SPUtils.get("test_expire_key", "失效"), Toast.LENGTH_SHORT).show()
        }
        deleteTxv.setOnClickListener {
            SPUtils.delete("test_expire_key")
            Toast.makeText(this, SPUtils.get("test_expire_key", "失效"), Toast.LENGTH_SHORT).show()
        }
    }
}
