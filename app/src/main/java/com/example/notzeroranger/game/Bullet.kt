package com.example.notzeroranger.game

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.example.notzeroranger.R

open class Bullet(open var x: Float, open var y: Float, open val direction: Float) {
    open val speed = 10f
    open val damage = 10f
    open val paint = Paint().apply { color = Color.parseColor("#0000FF")}

    open fun update() {
        y -= speed * direction
    }

    open fun draw(canvas: Canvas) {
        canvas.drawCircle(x, y, 5f, paint)
    }

    fun isOffscreen(screenHeight: Int): Boolean {
        return y < 0 || y > screenHeight
    }
}

// FastBullet class
class PlayerBullet(context: Context, x: Float, y: Float, direction: Float) : Bullet(x, y, direction) {
    private val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.player_bullet)

    override fun draw(canvas: Canvas) {
        canvas.drawBitmap(bitmap, x, y, null)
    }
}

// BigBullet class
class BigBullet(x: Float, y: Float, direction: Float) : Bullet(x, y, direction) {
    override fun draw(canvas: Canvas) {
        canvas.drawCircle(x, y, 10f, paint)
    }
}