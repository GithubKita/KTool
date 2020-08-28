package com.kita.ktool.ui

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.kita.ktool.R
import com.kita.ktool.util.BitmapUtil
import java.io.File
import java.io.FileOutputStream

/**
 * 作者：PC on 2020/8/28 15:10
 * 邮箱：wang_kita@163.com
 * 抠图，从一张透明背景图片中提取出内容区域，生成新的图
 */
class ImgCutoutActivity :AppCompatActivity(){
    private lateinit var tvInfo: TextView
    private lateinit var imgOriginal: ImageView
    private lateinit var imgNew: ImageView
    private lateinit var btnZoom: Button
    private var newBitmap: Bitmap? = null
    private val zoomIn = 3 / 2f
    private val zoomOut = 2 / 3f
    private var isZoomIn = false

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_img_cutout)
        initActionBar()
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
    
    @RequiresApi(Build.VERSION_CODES.O)
    fun initView(){
        tvInfo = findViewById(R.id.tvInfo)
        imgOriginal = findViewById(R.id.imgOriginal)
        imgNew = findViewById(R.id.imgNew)
        btnZoom = findViewById(R.id.btnZoom)
        imgOriginal.setImageResource(R.drawable.test)
        findViewById<View>(R.id.btnCrop)
            .setOnClickListener {
                startCrop()
            }
        findViewById<View>(R.id.btnRotate)
            .setOnClickListener { rotate() }
        findViewById<View>(R.id.btnZoom)
            .setOnClickListener { zoom() }
        findViewById<View>(R.id.btnRed)
            .setOnClickListener { changeTextColor(newBitmap, Color.RED) }
        findViewById<View>(R.id.btnYellow)
            .setOnClickListener { changeTextColor(newBitmap, Color.YELLOW) }
        findViewById<View>(R.id.btnBlue)
            .setOnClickListener { changeTextColor(newBitmap, Color.BLUE) }
        findViewById<View>(R.id.btnSave)
            .setOnClickListener {
                try {
                    saveAndShare()
                } catch (e: Exception) {
                    e.printStackTrace()
                    tvInfo.text = "保存图片失败!"
                }
            }
    }

    /**
     * 旋转
     */
    private fun rotate() {
        if (newBitmap == null) return
        newBitmap = BitmapUtil.rotateBitmap(newBitmap!!, 30)
        imgNew.setImageBitmap(newBitmap)
    }

    /**
     * 放大
     */
    private fun zoom() {
        if (newBitmap == null) return
        if (!isZoomIn) {
            newBitmap = BitmapUtil.zoomBitmap(newBitmap!!, zoomIn)
            btnZoom.text = "缩小"
            isZoomIn = true
        } else {
            newBitmap = BitmapUtil.zoomBitmap(newBitmap!!, zoomOut)
            btnZoom.text = "放大"
            isZoomIn = false
        }
        imgNew.setImageBitmap(newBitmap)
    }

    /**
     * 截取
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun startCrop() {
        try {
            val bitmap = BitmapFactory.decodeResource(resources, R.drawable.test)
            val rect = getTextRectFromBitmap(bitmap)
            cropTextFromBitmap(bitmap, rect)
            Toast.makeText(baseContext, "截取成功", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            tvInfo.text = "文字截取失败！" + e.message
        }
    }


    /**
     * 获取文字区域
     *
     * @param bitmap
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    fun getTextRectFromBitmap(bitmap: Bitmap): Rect {
        val width = bitmap.width
        val height = bitmap.height
        val left = findLeft(width, height, bitmap)
        val right = findRight(width, height, bitmap)
        val top = findTop(width, height, bitmap)
        val bottom = findBottom(width, height, bitmap)
        return Rect(left, top, right, bottom)
    }

    /**
     * 截取文字部分
     *
     * @param bitmap
     * @param rect
     * @throws Exception
     */
    @Throws(Exception::class)
    private fun cropTextFromBitmap(bitmap: Bitmap, rect: Rect) {
        newBitmap = Bitmap.createBitmap(bitmap, rect.left, rect.top, rect.width(), rect.height())
        imgNew.setImageBitmap(newBitmap)
    }

    /**
     *
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun changeTextColor(bitmap: Bitmap?, newColor: Int) {
        if (bitmap == null) return
        val width = bitmap.width
        val height = bitmap.height
        val length = width * height
        var index = 0
        val colorArray = IntArray(length)
        for (j in 0 until height) {
            for (i in 0 until width) {
                val color = bitmap.getPixel(i, j)
                val a = Color.alpha(color)
                colorArray[index] = if (a == 0) color else newColor
                index++
            }
        }
        newBitmap = Bitmap.createBitmap(colorArray, width, height, Bitmap.Config.ARGB_4444)
        imgNew.setImageBitmap(newBitmap)
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
    fun saveImage(
        recBitmap: Bitmap?,
        outPath: String,
        type: Bitmap.CompressFormat?,
        quality: Int
    ): String? {
        val fos = FileOutputStream(outPath)
        recBitmap!!.compress(type, quality, fos)
        fos.flush()
        fos.close()
        tvInfo.text = "文件已保存至: $outPath"
        return outPath
    }

    /**
     * 找出左边
     *
     * @param width
     * @param height
     * @param bitmap
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun findLeft(width: Int, height: Int, bitmap: Bitmap): Int {
        for (i in 0 until width) {
            for (j in 0 until height) {
                val color = bitmap.getColor(i, j)
                if (color.alpha() != 0f) {
                    return i
                }
            }
        }
        return 0
    }

    /**
     * 找出右边
     *
     * @param width
     * @param height
     * @param bitmap
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun findRight(width: Int, height: Int, bitmap: Bitmap): Int {
        for (i in width - 1 downTo 1) {
            for (j in 0 until height) {
                val color = bitmap.getColor(i, j)
                if (color.alpha() != 0f) {
                    return i
                }
            }
        }
        return 0
    }


    /**
     * 找出上边
     *
     * @param width
     * @param height
     * @param bitmap
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun findTop(width: Int, height: Int, bitmap: Bitmap): Int {
        for (i in 0 until height) {
            for (j in 0 until width) {
                val color = bitmap.getColor(j, i)
                if (color.alpha() != 0f) {
                    return i
                }
            }
        }
        return 0
    }

    /**
     * 找出下边
     *
     * @param width
     * @param height
     * @param bitmap
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun findBottom(width: Int, height: Int, bitmap: Bitmap): Int {
        for (i in height - 1 downTo 1) {
            for (j in 0 until width) {
                val color = bitmap.getColor(j, i)
                if (color.alpha() != 0f) {
                    return i
                }
            }
        }
        return 0
    }

    /**
     * 保存并分享
     * @throws Exception
     */
    @Throws(Exception::class)
    private fun saveAndShare() {
        val file = File(
            getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            System.currentTimeMillis().toString() + ".png"
        )
        saveImage(newBitmap, file.absolutePath, Bitmap.CompressFormat.PNG, 100)
        val uri = FileProvider.getUriForFile(
            baseContext,
            baseContext.packageName + ".fileprovider",
            file
        )
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "image/png"
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        startActivity(Intent.createChooser(intent, "分享图片"))
    }

}