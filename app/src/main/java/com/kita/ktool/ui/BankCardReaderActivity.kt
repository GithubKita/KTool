package com.kita.ktool.ui

import android.R.attr
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.hjq.permissions.OnPermission
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.kita.ktool.R
import io.card.payment.CardIOActivity
import io.card.payment.CreditCard


/**

 * 作者：PC on 2020/8/29 11:32
 * 邮箱：wang_kita@163.com
 * 扫描银行卡，提取卡号
 */
class BankCardReaderActivity: AppCompatActivity(){

    val MY_SCAN_REQUEST_CODE = 100
    private lateinit var tvResult: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bank_card)
        initActionBar()
        findViewById<Button>(R.id.btnRead).setOnClickListener {
            requestPermission()

        }
        findViewById<Button>(R.id.btnCopy).setOnClickListener {
            tvResult.text?.let {
                copyResult(it.toString())
                Toast.makeText(this@BankCardReaderActivity, "已复制到剪切板！", Toast.LENGTH_SHORT).show()
            }

        }
        tvResult = findViewById(R.id.tvResult)
    }

    private fun initActionBar() {
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    /**
     * 检测权限
     */
    private fun requestPermission() {
        XXPermissions.with(this).permission(Permission.CAMERA).request(object : OnPermission {
            override fun noPermission(denied: MutableList<String>?, quick: Boolean) {
                if (quick) {
                    Toast.makeText(baseContext, "被永久拒绝授权，请手动授予存储和拍照权限", Toast.LENGTH_SHORT)
                    // 如果是被永久拒绝就跳转到应用权限系统设置页面
                    XXPermissions.startPermissionActivity(this@BankCardReaderActivity, denied)
                } else {
                    Toast.makeText(baseContext, "获取存储和拍照权限失败", Toast.LENGTH_SHORT)
                }
            }

            override fun hasPermission(granted: MutableList<String>?, all: Boolean) {
                if (all) {
                    startReader()
                    Toast.makeText(baseContext, "拍照权限成功", Toast.LENGTH_SHORT)
                } else {
                    Toast.makeText(baseContext, "获取权限成功，部分权限未正常授予", Toast.LENGTH_SHORT)
                }
            }

        })
    }

    private fun startReader(){
        val scanIntent = Intent(this, CardIOActivity::class.java)
        // customize these values to suit your needs.
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true) // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, false) // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false) // default: false
        // MY_SCAN_REQUEST_CODE is arbitrary and is only used within this activity.
        startActivityForResult(scanIntent, MY_SCAN_REQUEST_CODE)
    }

    /**
     * 复制到剪切板
     */
    private fun copyResult(result: String) {
        var clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        var clipData = ClipData.newPlainText(null, result)
        clipboardManager.setPrimaryClip(clipData)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode === MY_SCAN_REQUEST_CODE) {
            var resultDisplayStr: String
            if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
                val scanResult: CreditCard =
                    data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT)
                // Never log a raw card number. Avoid displaying it, but if necessary use getFormattedCardNumber()
                resultDisplayStr = """
                    Card Number: ${scanResult.redactedCardNumber}
                    """.trimIndent()
                // Do something with the raw number, e.g.:
                // myService.setCardNumber( scanResult.cardNumber );
                if (scanResult.isExpiryValid) {
                    resultDisplayStr += """
                        Expiration Date: ${scanResult.expiryMonth}/${scanResult.expiryYear}
                        """.trimIndent()
                }
                if (scanResult.cvv != null) {
                    // Never log or display a CVV
                    resultDisplayStr += """CVV has ${scanResult.cvv.length} digits."""
                }
                if (scanResult.postalCode != null) {
                    resultDisplayStr += """
                        Postal Code: ${scanResult.postalCode}
                        """.trimIndent()
                }
            } else {
                resultDisplayStr = "Scan was canceled."
            }
            // do something with resultDisplayStr, maybe display it in a textView
            tvResult.text = resultDisplayStr
        }
    }
}