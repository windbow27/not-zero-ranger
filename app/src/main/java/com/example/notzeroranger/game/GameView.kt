package com.example.notzeroranger.game

import Player
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.core.content.res.ResourcesCompat
import com.example.notzeroranger.GameActivity
import com.example.notzeroranger.GameOverActivity
import com.example.notzeroranger.R
import com.example.notzeroranger.database.DemoDbHeper
import com.example.notzeroranger.database.HighScore
import kotlin.random.Random

class GameView(context: Context) : SurfaceView(context), SurfaceHolder.Callback {
    private var gameLoopThread: GameLoopThread? = null
    private val displayMetrics = context.resources.displayMetrics
    private val screenWidth = displayMetrics.widthPixels
    private val screenHeight = displayMetrics.heightPixels
    private  val playerX = screenWidth / 2f
    private val playerY = screenHeight * 0.9f
    private val player = Player(context, playerX, playerY, 50f, 50f)


    companion object {
       var enemies = mutableListOf<Enemy>()
    }


    init {
        holder.addCallback(this)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        enemies.clear()
        player.clearBullets()
        for (enemy in enemies) {
            enemy.clearBullets()
        }
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

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        player.moveTo(event?.x ?: 0f, event?.y ?: 0f)
        return true
    }
}

class GameLoopThread(private val surfaceHolder: SurfaceHolder, private val context: Context, private val player: Player, private val enemies: MutableList<Enemy>) : Thread() {
    private var running = false
    private val displayMetrics = context.resources.displayMetrics
    private val background = BitmapFactory.decodeResource(context.resources, R.drawable.stage_background)
    private val smallEnemyPoints = 100
    private val bigEnemyPoints = 200
    private var lastWaveSpawnTime = System.currentTimeMillis()
    private val waveSpawnCooldown = 5600

    private val originalHealthBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.health)
    private val originalBlankBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.blank)
    private val bitmapWidth = displayMetrics.widthPixels / 16
    private val healthBitmapHeight = bitmapWidth * originalHealthBitmap.height / originalHealthBitmap.width
    private val blankBitmapHeight = bitmapWidth * originalBlankBitmap.height / originalBlankBitmap.width

    private val healthBitmap = Bitmap.createScaledBitmap(
        originalHealthBitmap,
        bitmapWidth,
        healthBitmapHeight,
        true
    )
    private val blankBitmap = Bitmap.createScaledBitmap(
        originalBlankBitmap,
        bitmapWidth,
        blankBitmapHeight,
        true
    )
    private val pixelloidTypeface = ResourcesCompat.getFont(context, R.font.pixelloid_font)
    private val paint = Paint().apply {
        color = context.resources.getColor(R.color.orange, null)
        textSize = 32f
        typeface = pixelloidTypeface
        style = Paint.Style.FILL_AND_STROKE
        strokeWidth = 2f
        setShadowLayer(1f, 0f, 0f, Color.BLACK)
    }

    fun setRunning(isRunning: Boolean) {
        running = isRunning
    }

    private fun drawPlayerStats(canvas: Canvas) {
        val gameActivity = context as GameActivity
        val health = (player.health / 10).toInt()
        val points = player.getPoints().toString()

        for (i in 0 until health) {
            canvas.drawBitmap(healthBitmap, 20f + i * healthBitmap.width, 20f, null)
        }

        for (i in 0 until gameActivity.getSensorChangeCount()) {
            canvas.drawBitmap(blankBitmap, 20f + i * blankBitmap.width, 60f, null)
        }

        canvas.drawText(points, canvas.width - 20f - paint.measureText(points), 50f, paint)
    }

    private fun spawnEnemies(context: Context, screenWidth: Int, screenHeight: Int) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastWaveSpawnTime >= waveSpawnCooldown) {
            val totalPoints = (800..1200).random()
            var remainingPoints = totalPoints

            while (remainingPoints >= smallEnemyPoints) {
                val x = Random.nextFloat() * screenWidth
                val y = Random.nextFloat() * screenHeight / 6

                val enemy = if (remainingPoints >= bigEnemyPoints && Random.nextBoolean()) {
                    remainingPoints -= bigEnemyPoints
                    BigEnemy(context, x, y, 100f, 100f, player)
                } else {
                    remainingPoints -= smallEnemyPoints
                    SmallEnemy(context, x, y, 50f, 50f, player)
                }

                enemies.add(enemy)
            }

            lastWaveSpawnTime = currentTime
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
                        val dbHelper = DemoDbHeper(context)
                        val db = dbHelper.writableDatabase
                        val values = ContentValues()
                        values.put("name", Player.name)
                        values.put("score", player.getPoints())
                        db.insert(HighScore.PlayerEntry.TABLE_NAME, "", values)
                        //db.delete(HighScore.PlayerEntry.TABLE_NAME,null,null)
                        db.close()
                    }
                }
                surfaceHolder.unlockCanvasAndPost(canvas)
            }
            if (!running) {
                val intent = Intent(context, GameOverActivity::class.java)
                intent.putExtra("SCORE", player.getPoints())
                context.startActivity(intent)
                break
            }
        }
    }
}