package com.okamikosoft.pdf_signature_sdk.customView

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class SignatureView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private var paint: Paint = Paint()
    private var path: Path = Path()
    private var bitmap: Bitmap? = null
    private var canvas: Canvas? = null

    init {
        paint.color = Color.BLACK
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 8f
        paint.isAntiAlias = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawPath(path, paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                path.moveTo(x, y)
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                path.lineTo(x, y)
            }
            MotionEvent.ACTION_UP -> {
                // Kullanıcı çizimi bitirdiğinde, bitmap'e kaydedelim
                saveToBitmap()
            }
        }
        invalidate()
        return true
    }

    private fun saveToBitmap() {
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        canvas = Canvas(bitmap!!)
        canvas!!.drawColor(Color.WHITE)
        canvas!!.drawPath(path, paint)
    }

    fun getSignatureBitmap(): Bitmap? {
        return bitmap
    }

    fun clearSignature() {
        path.reset()
        invalidate()
    }
}
