package com.kita.ktool.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.kita.ktool.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.btnImgCutOut).setOnClickListener {
            val intent = Intent(this, ImgCutoutActivity::class.java)
            startActivity(intent)
        }
        findViewById<View>(R.id.btnHtmlToImg).setOnClickListener {
            val intent = Intent(this, HtmlToImgActivity::class.java)
            startActivity(intent)
        }

        findViewById<View>(R.id.btnQRCode).setOnClickListener {
            val intent = Intent(this, QRCodeActivity::class.java)
            startActivity(intent)
        }
    }
}
