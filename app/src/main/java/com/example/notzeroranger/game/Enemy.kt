package com.example.notzeroranger.game

import Player
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import kotlin.random.Random

open class Enemy(x: Float, y: Float, width: Float, height: Float, val player: Player) : GameObject(x, y, width, height) {
    open var bullet: Bullet? = null
    private val direction = Random.nextFloat() - 0.5f;
    val bullets = mutableListOf<Bullet>()
    private var lastShotTime = System.currentTimeMillis()
    open val shootCooldown = 1000

    fun draw(canvas: Canvas) {
        val paint = Paint()
        paint.color = Color.RED // Change this to the color you want for the enemy
        canvas.drawRect(getBoundingBox(), paint)
    }

    override fun getBoundingBox(): RectF {
        return RectF(x, y, x + width, y + height)
    }

    fun move() {
        x += direction * speed
        y += speed
    }

    fun isAlive(): Boolean {
        return health > 0
    }

    fun isOffscreen(screenHeight: Int): Boolean {
        return y > screenHeight
    }

    fun killIfOffscreen(screenHeight: Int) {
        if (isOffscreen(screenHeight)) {
            health = 0f
        }
    }
    open fun shoot() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastShotTime >= shootCooldown) {
            val bullet = Bullet(x + width / 2, y + height, player.x, player.y)
            bullets.add(bullet)
            lastShotTime = currentTime
        }
    }

    fun updateBullets(screenHeight: Int) {
        bullets.removeAll { it.isOffscreen(screenHeight) }
        bullets.forEach { it.update() }
    }

    fun drawBullets(canvas: Canvas) {
        bullets.forEach { it.draw(canvas) }
    }
}

class SmallEnemy(x: Float, y: Float, width: Float, height: Float, player: Player) : Enemy(x, y, width, height, player) {
    override var speed = 2f
    override var health = 50f
    private var lastShotTime = System.currentTimeMillis()

    override fun shoot() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastShotTime >= shootCooldown) {
            val bullet = SmallBullet(x, y, player.x, player.y)
            bullets.add(bullet)
            lastShotTime = currentTime
        }
    }
}

class BigEnemy(x: Float, y: Float, width: Float, height: Float, player: Player) : Enemy(x, y, width, height, player) {
    override var speed = 1f
    override var health = 200f
    private var lastShotTime = System.currentTimeMillis()

    override fun shoot() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastShotTime >= shootCooldown) {
            val bullet = BigBullet(x, y, player.x, player.y)
            bullets.add(bullet)
            lastShotTime = currentTime
        }
    }
}