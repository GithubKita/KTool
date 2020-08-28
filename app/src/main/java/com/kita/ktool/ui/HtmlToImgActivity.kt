package com.kita.ktool.ui

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.kita.ktool.R
import com.kita.ktool.util.BitmapUtil
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**

 * 作者：PC on 2020/8/28 15:27
 * 邮箱：wang_kita@163.com
 * 将网页转成图片
 */
class HtmlToImgActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var input: EditText

    private var mUrl = "https://www.baidu.com/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_html_to_img)
        initActionBar()
        initWebview()
        initView()
    }

    private fun initActionBar(){
        supportActionBar?.apply {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    private fun initView(){
        input = findViewById(R.id.input)
        input.setText(mUrl)
        findViewById<View>(R.id.btnStart).setOnClickListener {
            mUrl = input.text.toString()
            if (TextUtils.isEmpty(mUrl)) {
                Toast.makeText(baseContext, "请输入链接", Toast.LENGTH_SHORT).show()
            }
            webView.loadUrl(mUrl)
        }
        findViewById<View>(R.id.btnShare).setOnClickListener(View.OnClickListener {
            saveAndShare(webView)
        })
        findViewById<View>(R.id.btnClear).setOnClickListener(View.OnClickListener { input.setText("") })
    }

    private fun initWebview(){
        webView = findViewById(R.id.webView)
        val mWebSettings: WebSettings = webView.settings
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            mWebSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            mWebSettings.mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
        }
        mWebSettings.blockNetworkImage = false
        mWebSettings.javaScriptEnabled = true
        mWebSettings.builtInZoomControls = true
        mWebSettings.setSupportZoom(false)
        mWebSettings.loadWithOverviewMode = true
        mWebSettings.useWideViewPort = true
        mWebSettings.cacheMode = WebSettings.LOAD_NO_CACHE
        mWebSettings.defaultTextEncodingName = "UTF-8"
        mWebSettings.domStorageEnabled = true

        webView.loadUrl(mUrl)
    }

    /**
     * 保存图片
     *
     * @param recBitmap
     * @param outPath
     * @param type      JPEG 格式会丢失透明度,大小更小, 需要透明度的地方传PNG
     * @return
     */
    @Throws(Exception::class)
    fun saveImage(recBitmap: Bitmap, outPath: String, type: CompressFormat, quality: Int): String? {
        try {
            val fos = FileOutputStream(outPath)
            recBitmap.compress(type, quality, fos)
            fos.flush()
            fos.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return outPath
    }

    /**
     * 保存并分享
     *
     * @throws Exception
     */
    private fun saveAndShare(view: View) {
        GlobalScope.launch {
            try {
                val file = File(
                    getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    System.currentTimeMillis().toString() + ".jpg"
                )
                var bitmap = htmlToBitmap(view)
                bitmap?.let {
                    saveImage(
                        it,
                        file.absolutePath,
                        CompressFormat.JPEG,
                        100
                    )
                }
                val uri = FileProvider.getUriForFile(
                    applicationContext, "$packageName.fileprovider",
                    file
                )
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "image/jpg"
                intent.putExtra(Intent.EXTRA_STREAM, uri)
                startActivity(Intent.createChooser(intent, "分享图片"))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun htmlToBitmap(view: View): Bitmap?{
        return BitmapUtil.bitmapFromView(view)
    }
}