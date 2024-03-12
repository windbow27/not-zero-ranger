package com.example.notzeroranger.game

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import com.example.notzeroranger.R

open class Bullet(open var x: Float, open var y: Float, playerX: Float, playerY: Float) {
    // Calculate direction vector
    private val directionX = playerX - x
    private val directionY = playerY - y
    // Normalize direction vector
    private val length = Math.sqrt((directionX * directionX + directionY * directionY).toDouble()).toFloat()
    open val direction = PointF(directionX / length, directionY / length)
    open val speed = 10f
    open val damage = 10f
    open val paint = Paint().apply { color = Color.parseColor("#0000FF")}

    open fun update() {
        x += speed * direction.x
        y += speed * direction.y
    }

    open fun draw(canvas: Canvas) {
        canvas.drawCircle(x, y, 5f, paint)
    }

    fun getBoundingBox(): RectF {
        return RectF(x,y,x+5f, y+5f)
    }

    fun isOffscreen(screenHeight: Int, screenWidth: Int): Boolean {
        return y < 0 || y > screenHeight || x < 0 || x > screenWidth
    }
}

class PlayerBullet(context: Context, x: Float, y: Float) : Bullet(x, y, x, 0f) {
    private val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.player_bullet)

    override fun draw(canvas: Canvas) {
        canvas.drawBitmap(bitmap, x, y, null)
    }
}

class SmallBullet(context:Context, x: Float, y: Float, playerX: Float, playerY: Float) : Bullet(x, y, playerX, playerY) {
    private val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.small_bullet)
    override fun draw(canvas: Canvas) {
        canvas.drawBitmap(bitmap, x, y, null)
    }
}

class BigBullet(context:Context, x: Float, y: Float, playerX: Float, playerY: Float) : Bullet(x, y, playerX, playerY) {
    private val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.big_bullet)
    override fun draw(canvas: Canvas) {
        canvas.drawBitmap(bitmap, x, y, null)
    }
}