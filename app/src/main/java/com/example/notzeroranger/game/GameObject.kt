package com.example.notzeroranger.game

import android.graphics.RectF
import android.util.Log

open class GameObject(var x: Float, var y: Float, open var width: Float, var height: Float) {
    open var speed = 0f
    open var health = 0f

    open fun getBoundingBox(): RectF {
        return RectF(x, y, x + width, y + height)
    }

    open fun reduceHealth(bullet: Bullet) {
        health -= bullet.damage
        Log.d("GameObject", "Health: $health")
    }
}