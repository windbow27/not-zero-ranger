package com.example.notzeroranger.setting

object HealthManger {
    private var health : Float = 30f;

    fun setHealth(health : Int) {
        this.health = health.toFloat() * 10;
    }

    fun getHealth(): Float {
        return health;
    }
}