package com.kita.ktool.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.view.View

/**

 * 作者：PC on 2020/8/28 14:19
 * 邮箱：wang_kita@163.com
 */
object BitmapUtil {

    /**
     * 图片缩放
     */
    fun zoomBitmap(source: Bitmap, scale: Float): Bitmap? {
        val matrix = Matrix()
        matrix.setScale(scale, scale)
        val result =
            Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
        source.recycle()
        return result
    }

    /**
     * 旋转图片
     */
    fun rotateBitmap(source: Bitmap, degrees: Int): Bitmap? {
        val matrix = Matrix()
        val width = source.width
        val height = source.height
        matrix.setRotate(degrees.toFloat(), width / 2.toFloat(), height / 2.toFloat())
        val result = Bitmap.createBitmap(source, 0, 0, width, height, matrix, true)
        source.recycle()
        return result
    }

    fun bitmapFromView(view: View): Bitmap? {
        try {
            val screenshot: Bitmap
            val viewHeight = view.height
            val maxHeight = Math.min(viewHeight, 15000) //最大高度
            screenshot = Bitmap.createBitmap(view.width, viewHeight, Bitmap.Config.ARGB_4444)
            val canvas = Canvas(screenshot)
            canvas.translate(-view.scrollX.toFloat(), -view.scrollY.toFloat())
            //我们在用滑动View获得它的Bitmap时候，获得的是整个View的区域（包括隐藏的），如果想得到当前区域，需要重新定位到当前可显示的区域
            view.draw(canvas) // 将 view 画到画布上
            return screenshot
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}