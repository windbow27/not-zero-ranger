package com.example.notzeroranger.game

import android.graphics.RectF
import android.util.Log

open class GameObject(var x: Float, var y: Float, open var width: Float, var height: Float) {
    open var speed = 0f
    open var health = 0f
    open var bullets = mutableListOf<Bullet>()

    open fun getBoundingBox(): RectF {
        return RectF(x, y, x + width, y + height)
    }

    open fun reduceHealth(bullet: Bullet) {
        health -= bullet.damage
        Log.d("GameObject", "Health: $health")
    }

    open fun isAlive(): Boolean {
        return health > 0
    }

    open fun checkCollision(gameObjects: List<GameObject>) {
        bullets.removeIf { bullet ->
            gameObjects.any { gameObject ->
                if (RectF.intersects(bullet.getBoundingBox(), gameObject.getBoundingBox())) {
                    // Handle collision
                    gameObject.reduceHealth(bullet)
                    true
                } else {
                    false
                }
            }
        }
    }
}