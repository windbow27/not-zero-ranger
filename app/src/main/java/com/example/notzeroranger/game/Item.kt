package com.example.notzeroranger.game

import Player
import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF
import com.example.notzeroranger.R
import pl.droidsonroids.gif.GifDrawable
import kotlin.random.Random

open class Item (context: Context, x: Float, y:Float, width: Float, height: Float, val player: Player) :GameObject(x , y , width, height) {
    private val direction = Random.nextFloat() - 0.5f
    open var touch = false
    open val itemDrawable = GifDrawable(context.resources , R.drawable.player_gif)

    fun draw(canvas: Canvas) {
        itemDrawable.setBounds(x.toInt(), y.toInt(), (x + width).toInt(), (y + height).toInt())
        itemDrawable.draw(canvas)
    }

    override fun getBoundingBox(): RectF {
        return RectF(x, y, x + width, y + height)
    }

    fun move() {
        x += direction * speed
        y += speed
    }

    fun isOffscreen(screenHeight: Int, screenWidth: Int): Boolean {
        return y > screenHeight || x < 0 || x > screenWidth
    }
}

class HealthItem(val context: Context, x:Float, y: Float, width: Float, height: Float, player: Player) : Item(context, x, y, width, height, player) {
    override var speed = 3f
    override var touch = false
    override val itemDrawable = GifDrawable(context.resources, R.drawable.healthitem)
}

class SpeechItem(val context: Context, x:Float, y: Float, width: Float, height: Float, player: Player) : Item(context, x, y, width, height, player) {
    override var speed = 2f
    override var touch = false
    override val itemDrawable = GifDrawable(context.resources, R.drawable.speech)
}