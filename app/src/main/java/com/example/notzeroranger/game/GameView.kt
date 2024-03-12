package com.example.notzeroranger.game

import Player
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.RectF
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.example.notzeroranger.R
import kotlin.random.Random

class GameView(context: Context) : SurfaceView(context), SurfaceHolder.Callback {
    private var gameLoopThread: GameLoopThread? = null
    private val player = Player(context)
    private val enemies = mutableListOf<Enemy>()

    init {
        holder.addCallback(this)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        gameLoopThread = GameLoopThread(holder, context, player, enemies)
        gameLoopThread?.setRunning(true)
        gameLoopThread?.start()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
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

class GameLoopThread(private val surfaceHolder: SurfaceHolder, context: Context, private val player: Player, private val enemies: MutableList<Enemy>) : Thread() {
    private var running = false
    private val background = BitmapFactory.decodeResource(context.resources, R.drawable.stage_background)

    private val enemySpawnCooldown = 2000
    private var lastEnemySpawnTime = System.currentTimeMillis()

    fun setRunning(isRunning: Boolean) {
        running = isRunning
    }

    private fun spawnEnemies(screenWidth: Int, screenHeight: Int) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastEnemySpawnTime >= enemySpawnCooldown) {
            val x = Random.nextFloat() * screenWidth
            val y = 0f
            val enemy = if (Random.nextBoolean()) {
                SmallEnemy(x, y,50f, 50f, player)
            } else {
                BigEnemy(x, y, 100f, 100f, player)
            }
            enemies.add(enemy)
            lastEnemySpawnTime = currentTime
        }
    }

    override fun run() {
        while (running) {
            val canvas = surfaceHolder.lockCanvas(null)
            if (canvas != null) {
                synchronized(surfaceHolder) {
                    // Clear the canvas
                    canvas.drawBitmap(background, null, RectF(0f, 0f, canvas.width.toFloat(), canvas.height.toFloat()), null)

                    // Update game state
                    player.moveTo(player.x, player.y)

                    spawnEnemies(canvas.width, canvas.height)

                    // Player is always shooting in the same direction
                    player.shoot()
                    player.update()

                    player.checkCollision(enemies.filter { it.isAlive() })

                    // Draw the game state to the canvas
                    enemies.forEach {
                        it.updateBullets(canvas.height)
                        it.drawBullets(canvas)
                        if (it.isAlive()) {
                            it.shoot()
                            it.move()
                            it.draw(canvas)
                        }
                        it.killIfOffscreen(canvas.height)
                    }
                    player.draw(canvas)
                }
                surfaceHolder.unlockCanvasAndPost(canvas)
            }
        }
    }
}