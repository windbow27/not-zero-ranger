package com.example.notzeroranger.game

import Player
import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF
import com.example.notzeroranger.R
import pl.droidsonroids.gif.GifDrawable
import kotlin.random.Random

open class Enemy(context: Context, x: Float, y: Float, width: Float, height: Float, val player: Player) : GameObject(x, y, width, height) {
    private val direction = Random.nextFloat() - 0.5f
    open var point = 100
    override var bullets = mutableListOf<Bullet>()
    open var lastShotTime = System.currentTimeMillis()
    open val shootCooldown = 1000
    open val enemyDrawable = GifDrawable(context.resources, R.drawable.enemy_small)
    var pointsAdded = false


    fun draw(canvas: Canvas) {
        enemyDrawable.setBounds(x.toInt(), y.toInt(), (x + width).toInt(), (y + height).toInt())
        enemyDrawable.draw(canvas)
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

    fun killIfOffscreen(screenHeight: Int, screenWidth: Int) {
        if (isOffscreen(screenHeight, screenWidth)) {
            health = 0f
        }
    }
    open fun shoot() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastShotTime >= shootCooldown) {
            val bullet = Bullet(x + width / 2, y + height / 2, player.x, player.y)
            bullets.add(bullet)
            lastShotTime = currentTime
        }
    }

    fun updateBullets(screenHeight: Int, screenWidth: Int) {
        bullets.removeAll { it.isOffscreen(screenHeight, screenWidth) }
        bullets.forEach { it.update() }
        if (!isAlive() && !pointsAdded) {
            player.points += point
            pointsAdded = true
        }
    }

    fun drawBullets(canvas: Canvas) {
        bullets.forEach { it.draw(canvas) }
    }
}

class SmallEnemy(val context: Context, x:Float, y: Float, width: Float, height: Float, player: Player) : Enemy(context, x, y, width, height, player) {
    override var speed = 2f
    override var health = 50f
    override var point = 100
    override var lastShotTime = System.currentTimeMillis()
    override val enemyDrawable = GifDrawable(context.resources, R.drawable.enemy_small)

    override fun shoot() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastShotTime >= shootCooldown) {
            val bullet = SmallBullet(context, x +  width / 2, y + height / 2, player.x, player.y)
            bullets.add(bullet)
            lastShotTime = currentTime
        }
    }
}

class BigEnemy(val context: Context, x:Float, y: Float, width: Float, height: Float, player: Player) : Enemy(context, x, y, width, height, player) {
    override var speed = 1f
    override var health = 200f
    override var point = 200
    override var lastShotTime = System.currentTimeMillis()
    override val enemyDrawable = GifDrawable(context.resources, R.drawable.enemy_big)
    override fun shoot() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastShotTime >= shootCooldown) {
            val bullet = BigBullet(context, x +  width / 2, y + height / 2, player.x, player.y)
            bullets.add(bullet)
            lastShotTime = currentTime
        }
    }
}