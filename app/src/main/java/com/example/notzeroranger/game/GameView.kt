package com.example.notzeroranger.game

import Player
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView

class GameView(context: Context) : SurfaceView(context), SurfaceHolder.Callback {
    private var gameLoopThread: GameLoopThread? = null
    private val player = Player(context)

    init {
        holder.addCallback(this)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        gameLoopThread = GameLoopThread(holder, player)
        gameLoopThread?.setRunning(true)
        gameLoopThread?.start()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        // Adjust your game objects or view parameters here if needed
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        var retry = true
        gameLoopThread?.setRunning(false)
        while (retry) {
            try {
                gameLoopThread?.join()
                retry = false
            } catch (_: InterruptedException) {
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        player.moveTo(event?.x ?: 0f, event?.y ?: 0f)
        return true
    }
}

class GameLoopThread(private val surfaceHolder: SurfaceHolder, private val player: Player) : Thread() {
    private var running = false

    fun setRunning(isRunning: Boolean) {
        running = isRunning
    }

    override fun run() {
        while (running) {
            Log.d("GameLoopThread", "running")
            val canvas = surfaceHolder.lockCanvas(null)
            if (canvas != null) {
                synchronized(surfaceHolder) {
                    // Clear the canvas
                    val paint = Paint()
                    paint.color = Color.BLACK
                    canvas.drawRect(RectF(0f, 0f, canvas.width.toFloat(), canvas.height.toFloat()), paint)

                    // Update game state
                    player.moveTo(player.x, player.y)

                    // Player is always shooting in the same direction
                    player.shoot()
                    player.update()

                    // Draw the game state to the canvas
                    player.draw(canvas)
                }
                surfaceHolder.unlockCanvasAndPost(canvas)
            }
        }
    }
}