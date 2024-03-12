package com.example.notzeroranger.game

import Player
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.core.content.res.ResourcesCompat
import com.example.notzeroranger.GameOverActivity
import com.example.notzeroranger.R
import kotlin.random.Random

class GameView(context: Context) : SurfaceView(context), SurfaceHolder.Callback {
    private var gameLoopThread: GameLoopThread? = null
    val displayMetrics = context.resources.displayMetrics
    val screenWidth = displayMetrics.widthPixels
    val screenHeight = displayMetrics.heightPixels

    val playerX = screenWidth / 2f
    val playerY = screenHeight * 0.9f

    private val player = Player(context, playerX, playerY, 50f, 50f)
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

class GameLoopThread(private val surfaceHolder: SurfaceHolder, private val context: Context, private val player: Player, private val enemies: MutableList<Enemy>) : Thread() {
    private var running = false
    private val background = BitmapFactory.decodeResource(context.resources, R.drawable.stage_background)

    private val enemySpawnCooldown = 2000
    private var lastEnemySpawnTime = System.currentTimeMillis()

    val pixelloidTypeface = ResourcesCompat.getFont(context, R.font.pixelloid_font)
    private val paint = Paint().apply {
        color = Color.parseColor("#FF9800")
        textSize = 30f
        typeface = pixelloidTypeface
    }

    fun drawPlayerStats(canvas: Canvas) {
        val healthText = "HP: ${(player.health / 10).toInt()}"
        val pointsText = "Points: ${player.points}"

        canvas.drawText(healthText, 20f, canvas.height - 20f, paint)
        canvas.drawText(pointsText, canvas.width - 20f - paint.measureText(pointsText), 50f, paint)
    }

    fun setRunning(isRunning: Boolean) {
        running = isRunning
    }

    private fun spawnEnemies(context: Context, screenWidth: Int, screenHeight: Int) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastEnemySpawnTime >= enemySpawnCooldown) {
            val x = Random.nextFloat() * screenWidth
            val y = 0f
            val enemy = if (Random.nextBoolean()) {
                SmallEnemy(context, x, y,50f, 50f, player)
            } else {
                BigEnemy(context, x, y, 100f, 100f, player)
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

                    spawnEnemies(context, canvas.width, canvas.height)

                    // Player is always shooting in the same direction
                    player.shoot()
                    player.update()

                    player.checkCollision(enemies.filter { it.isAlive() })

                    // Draw the game state to the canvas
                    val iterator = enemies.iterator()
                    while (iterator.hasNext()) {
                        val enemy = iterator.next()
                        enemy.updateBullets(canvas.height, canvas.width)
                        enemy.drawBullets(canvas)
                        if (enemy.isAlive()) {
                            enemy.shoot()
                            enemy.move()
                            enemy.draw(canvas)
                        }
                        enemy.checkCollision(listOf(player))
                        enemy.killIfOffscreen(canvas.height, canvas.width)
                    }

                    player.draw(canvas)
                    drawPlayerStats(canvas)

                    if (!player.isAlive()) {
                        running = false
                    }
                }
                surfaceHolder.unlockCanvasAndPost(canvas)
            }
            if (!running) {
                val intent = Intent(context, GameOverActivity::class.java)
                context.startActivity(intent)
                break
            }
        }
    }
}