package com.kita.ktool.ui

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
import com.king.zxing.CaptureActivity
import com.king.zxing.Intents
import com.kita.ktool.R

/**

 * 作者：PC on 2020/8/28 18:21
 * 邮箱：wang_kita@163.com
 */
class QRCodeActivity: AppCompatActivity(){

    private lateinit var tvResult:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrcode)
        initActionBar()
        findViewById<Button>(R.id.btnScan).setOnClickListener {
            startScan()

        }
        findViewById<Button>(R.id.btnCopy).setOnClickListener {
            tvResult.text?.let {
                copyResult(it.toString())
                Toast.makeText(this@QRCodeActivity, "已复制到剪切板！", Toast.LENGTH_SHORT).show()
            }

        }
        tvResult = findViewById(R.id.tvResult)
    }

    private fun initActionBar(){
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    /**
     * 开始扫描
     */
    private fun startScan(){
        XXPermissions.with(this).permission(Permission.CAMERA).request(object :OnPermission{
            override fun noPermission(denied: MutableList<String>?, quick: Boolean) {
                if (quick) {
                    Toast.makeText(baseContext,"被永久拒绝授权，请手动授予存储和拍照权限", Toast.LENGTH_SHORT)
                    // 如果是被永久拒绝就跳转到应用权限系统设置页面
                    XXPermissions.startPermissionActivity(this@QRCodeActivity, denied)
                } else {
                    Toast.makeText(baseContext,"获取存储和拍照权限失败", Toast.LENGTH_SHORT)
                }
            }

            override fun hasPermission(granted: MutableList<String>?, all: Boolean) {
                if (all) {
                    startActivityForResult(Intent(baseContext, CaptureActivity::class.java), 100)
                    Toast.makeText(baseContext, "拍照权限成功", Toast.LENGTH_SHORT)
                } else {
                    Toast.makeText(baseContext, "获取权限成功，部分权限未正常授予", Toast.LENGTH_SHORT)
                }
            }

        })
    }

    /**
     * 复制到剪切板
     */
    private fun copyResult(result: String){
        var clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        var clipData = ClipData.newPlainText(null, result)
        clipboardManager.setPrimaryClip(clipData)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        data?.let {
            var result = it.getStringExtra(Intents.Scan.RESULT)
            tvResult.text = result
            println(result)

        }
    }

}