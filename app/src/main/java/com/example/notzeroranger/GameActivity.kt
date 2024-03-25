package com.example.notzeroranger

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.notzeroranger.game.GameView
import com.example.notzeroranger.game.GameView.Companion.enemies
import kotlin.math.abs

class GameActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var gameView: GameView
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var isItNotFirstTime: Boolean = false
    private var lastX: Float = 0f
    private var lastY: Float = 0f
    private var lastZ: Float = 0f
    private var differenceX: Float = 0f
    private var differenceY: Float = 0f
    private var differenceZ: Float = 0f
    private var shakeThreshold: Float = 5f
    private var sensorChangeCount = 2 // bomb count
    private var lastShakeTime: Long = 0
    private val shakeCooldown: Long = 5000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        gameView = GameView(this)
        setContentView(gameView)
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // Do something here if sensor accuracy changes.
    }

    override fun onSensorChanged(event: SensorEvent) {
        // The light sensor returns a single value.
        // Many sensors return 3 values, one for each axis.
        val currentX = event.values[0]
        val currentY = event.values[1]
        val currentZ = event.values[2]

        if(isItNotFirstTime) {
            differenceX = abs(lastX - currentX)
            differenceY = abs(lastY - currentY)
            differenceZ = abs(lastZ - currentZ)

            if (differenceX  > shakeThreshold && differenceY > shakeThreshold ||
                differenceX  > shakeThreshold && differenceZ > shakeThreshold ||
                differenceY  > shakeThreshold && differenceZ > shakeThreshold) {
                Log.i("Shake","Shake it off!")
                if (sensorChangeCount > 0) {
                    if (System.currentTimeMillis() - lastShakeTime > shakeCooldown) {
                        enemies.clear()
                        sensorChangeCount--
                        lastShakeTime = System.currentTimeMillis()
                    }
                }
                if (sensorChangeCount < 0) {
                    sensorChangeCount = 0
                }
            }
        }

        lastX = currentX
        lastY = currentY
        lastZ = currentZ
        isItNotFirstTime = true

        // Do something with this sensor value.
    }

    override fun onResume() {
        super.onResume()

        sensorManager.registerListener(this, accelerometer, 60)

    }
    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    fun getSensorChangeCount(): Int {
        return sensorChangeCount
    }

}