package com.example.notzeroranger.game

import Player
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.core.content.res.ResourcesCompat
import com.example.notzeroranger.GameActivity
import com.example.notzeroranger.GameOverActivity
import com.example.notzeroranger.MainActivity
import com.example.notzeroranger.R
import com.example.notzeroranger.database.DemoDbHeper
import com.example.notzeroranger.database.HighScore
import com.example.notzeroranger.service.GameSound
import com.example.notzeroranger.service.RetrofitInstance
import com.example.notzeroranger.service.ThemeSound
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.abs
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
        if (event != null && event.action == MotionEvent.ACTION_DOWN) {
            gameLoopThread?.handlePauseItemClick(event.x, event.y)
        }
        if (event != null && event.action == MotionEvent.ACTION_DOWN) {
            gameLoopThread?.handleResumeItemClick(event.x, event.y)
        }
        if (event != null && event.action == MotionEvent.ACTION_DOWN) {
            gameLoopThread?.handleMenuItemClick(event.x, event.y)
        }
        player.moveTo(event?.x ?: 0f, event?.y ?: 0f)
        return true
    }
}

class GameLoopThread(private val surfaceHolder: SurfaceHolder, private val context: Context, private val player: Player, private val enemies: MutableList<Enemy>) : Thread() {
    private var running = false
    private var paused = false
    private val displayMetrics = context.resources.displayMetrics

    private val background1 = BitmapFactory.decodeResource(context.resources, R.drawable.stage_background)
    private val background2 = BitmapFactory.decodeResource(context.resources, R.drawable.stage_background)
    private var bg1y = 0f
    private var bg2y = (-displayMetrics.heightPixels).toFloat()

    private val smallEnemyPoints = 100
    private val bigEnemyPoints = 200
    private var lastWaveSpawnTime = System.currentTimeMillis()
    private val waveSpawnCooldown = 5600

    private var speedItemEffectTime: Long = 0
    private val speedItemDuration = 20000
    private val items = mutableListOf<Item>()

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

    private lateinit var pauseButton: Bitmap
    private lateinit var pauseLayout: Bitmap
    private lateinit var menuButton: Bitmap
    private lateinit var resumeButton: Bitmap
    private  lateinit var pauseItem: Bitmap
    private var pauseButtonRect: Rect = Rect()
    private var pauseLayoutButtonRect: Rect = Rect()
    private var menuButtonRect: Rect = Rect()
    private var resumeButtonRect: Rect = Rect()
    private var pauseItemRect: Rect = Rect()

    init {
        val buttonWidth = 50
        val buttonHeight = 50
        // Load the pause button image
        pauseLayout = BitmapFactory.decodeResource(context.resources, R.drawable.background_menu)
        pauseItem = BitmapFactory.decodeResource(context.resources, R.drawable.pause_title)
        pauseButton = Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(context.resources, R.drawable.pause_button),
            buttonWidth,
            buttonHeight,
            true
        )
        menuButton = Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(context.resources, R.drawable.menu_button),
            buttonWidth,
            buttonHeight,
            true
        )

        resumeButton = Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(context.resources, R.drawable.resume_button),
            buttonWidth,
            buttonHeight,
            true
        )
    }

    private fun drawPauseItem(canvas: Canvas) {
        // draw the pause button
        if (::pauseButton.isInitialized) {
            val centerX = 15
            val centerY = 100

            pauseButtonRect.set(
                centerX,
                centerY,
                centerX + pauseButton.width ,
                centerY + pauseButton.height
            )

            canvas.drawBitmap(pauseButton, null, pauseButtonRect, null)
        }
        if(paused){
            if (::pauseLayout.isInitialized) {
                val pauseLayoutWidth = canvas.width * 0.6f
                val pauseLayoutHeight = pauseLayoutWidth * (pauseLayout.height.toFloat() / pauseLayout.width.toFloat())

                val pauseLayoutX = (canvas.width - pauseLayoutWidth) / 2
                val pauseLayoutY = (canvas.height - pauseLayoutHeight) / 2
                pauseLayoutButtonRect.set(pauseLayoutX.toInt(),
                    pauseLayoutY.toInt(), (pauseLayoutX + pauseLayoutWidth).toInt(), (pauseLayoutY + pauseLayoutHeight).toInt()
                )
                canvas.drawBitmap(pauseLayout, null, pauseLayoutButtonRect, null)

                if(::pauseItem.isInitialized) {
                    // place the pauseItem inside the pauseLayout
                    val pauseItemWidth = pauseLayoutWidth * 0.9f
                    val pauseItemHeight = pauseItemWidth * (pauseItem.height.toFloat() / pauseItem.width.toFloat())

                    val pauseItemX = pauseLayoutX + (pauseLayoutWidth - pauseItemWidth) / 2
                    val pauseItemY = pauseLayoutY - pauseItemHeight
                    pauseItemRect.set(pauseItemX.toInt(), pauseItemY.toInt(), (pauseItemX + pauseItemWidth).toInt(), (pauseItemY + pauseItemHeight).toInt())
                    canvas.drawBitmap(pauseItem, null, pauseItemRect, null)
                }

                // Đặt resumeButton vào trong pauseLayout
                if (::resumeButton.isInitialized && ::menuButton.isInitialized) {
                    val buttonSize = resumeButton.width.coerceAtMost(menuButton.width) + 20

                    val resumeButtonX = pauseLayoutX + (pauseLayoutWidth - buttonSize * 2) / 2 - 25f
                    val resumeButtonY = pauseLayoutY + (pauseLayoutHeight - buttonSize) / 2
                    resumeButtonRect.set(resumeButtonX.toInt(), resumeButtonY.toInt(), (resumeButtonX + buttonSize).toInt(), (resumeButtonY + buttonSize).toInt())
                    canvas.drawBitmap(resumeButton, null, resumeButtonRect, null)

                    val menuButtonX = resumeButtonX + buttonSize + 50f // menuButton next to resumeButton
                    menuButtonRect.set(
                        menuButtonX.toInt(),
                        resumeButtonY.toInt(),
                        (menuButtonX + buttonSize).toInt(),
                        (resumeButtonY + buttonSize).toInt()
                    )
                    canvas.drawBitmap(menuButton, null, menuButtonRect, null)
                }
            }

        }
    }

    fun handleResumeItemClick(x: Float, y: Float) {
        if (resumeButtonRect.contains(x.toInt(), y.toInt())) {
            paused = !paused
        }
    }


    fun handlePauseItemClick(x: Float, y: Float) {
        if (pauseButtonRect.contains(x.toInt(), y.toInt())) {
            paused = !paused
        }
    }

    fun handleMenuItemClick(x: Float ,  y:Float) {
        // check if the menu button is clicked
        if(menuButtonRect.contains(x.toInt(), y.toInt())) {
            val gameSound = Intent(context, GameSound::class.java)
            context.startService(gameSound)
            val intent = Intent(context , MainActivity::class.java)
            context.startActivity(intent)
            context.stopService(gameSound)
            val themeSound = Intent(context, ThemeSound::class.java)
            context.startService(themeSound)
        }
    }


    override fun run() {
        while (running) {
            if(!paused){
                val canvas = surfaceHolder.lockCanvas(null)
                if (canvas != null) {
                    synchronized(surfaceHolder) {
                        // Clear the canvas by drawing the moving background images
                        canvas.drawBitmap(background1, null, RectF(0f, bg1y, canvas.width.toFloat(), bg1y + canvas.height), null)
                        canvas.drawBitmap(background2, null, RectF(0f, bg2y, canvas.width.toFloat(), bg2y + canvas.height), null)

                        // Move the backgrounds
                        bg1y += displayMetrics.heightPixels * 0.01f
                        bg2y += displayMetrics.heightPixels * 0.01f

                        // If a background has moved off the screen, reset its position
                        if (bg1y > canvas.height) {
                            bg1y = -canvas.height.toFloat()
                        }
                        if (bg2y > canvas.height) {
                            bg2y = -canvas.height.toFloat()
                        }

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
                            } else if(!enemy.isEnemyDead){
                                val newItemX = enemy.x
                                val newItemY = enemy.y
                                val randomValue = (1..100).random()
                                val newItem = if (randomValue <= 3) {
                                    HealthItem(context, newItemX, newItemY, 50f, 50f, player)
                                } else if (randomValue <= 5){
                                    SpeedItem(context, newItemX, newItemY, 50f, 50f, player)
                                } else if (randomValue <= 7){
                                    BombItem(context, newItemX, newItemY, 50f, 50f, player)
                                }
                                else {
                                    null
                                }
                                if (newItem != null) {
                                    items.add(newItem)
                                }
                                // println("enemy died")
                                enemy.isEnemyDead = true
                            }
                            enemy.checkCollision(listOf(player))
                            enemy.killIfOffscreen(canvas.height, canvas.width)
                        }

                        var eatenItemType: String? = null

                        val itemIterator = items.iterator()
                        while (itemIterator.hasNext()) {
                            val item = itemIterator.next()
                            item.move()
                            item.draw(canvas) // Vẽ item
                            if (item.isOffscreen(canvas.height, canvas.width)) {
                                itemIterator.remove()
                            }
                            val touchRadius = 50

                            if (abs(player.x - item.x) < touchRadius && abs(player.y - item.y) < touchRadius) {
                                eatenItemType = if (item is HealthItem) {
                                    "HealthItem"
                                } else (when (item) {
                                    is SpeedItem -> {
                                        "SpeedItem"
                                    }

                                    is BombItem -> {
                                        "BombItem"
                                    }

                                    else -> {
                                        null
                                    }
                                }).toString()
                                itemIterator.remove()
                            }
                        }

                        if (eatenItemType != null) {
                            when (eatenItemType) {
                                "HealthItem" -> {
                                    if(player.health < 60f){
                                        player.health += 10f
                                    }
                                }
                                "SpeedItem" -> {
                                    // increase player speed
                                    player.speed += 10f
                                    println(player.speed)
                                    speedItemEffectTime = System.currentTimeMillis()
                                }
                                "BombItem" -> {
                                    // destroy all enemies
                                    enemies.forEach { it.health = 0f }
                                }
                                else -> {
                                    println("Unknown item type")
                                }
                            }
                        }

                        if (System.currentTimeMillis() - speedItemEffectTime > speedItemDuration) {
                            // return player speed to normal
                            player.speed = maxOf(player.speed - 10f, 20f)
                        }

                        player.draw(canvas)
                        drawPlayerStats(canvas)
                        drawPauseItem(canvas)

                        if (!player.isAlive()) {
                            running = false
                            val dbHelper = DemoDbHeper(context)
                            val db = dbHelper.writableDatabase
                            val values = ContentValues()
                            values.put("name", Player.name)
                            values.put("score", player.getPoints())
                            db.insert(HighScore.PlayerEntry.TABLE_NAME, "", values)
                            //db.delete(HighScore.PlayerEntry.TABLE_NAME,null,null)
                            val highscore = com.example.notzeroranger.highscore.HighScore(
                                Player.name,
                                player.getPoints().toLong()
                            )

                            // push new score to remote database
                            RetrofitInstance.api.pushData(highscore).enqueue(object :
                                Callback<com.example.notzeroranger.highscore.HighScore> {
                                override fun onResponse(call: Call<com.example.notzeroranger.highscore.HighScore>, response: Response<com.example.notzeroranger.highscore.HighScore>) {
                                    if (response.isSuccessful) {
                                        val data = response.body()
                                        Log.d("SCORE", "Pushed data successfully: ${data.toString()}")
                                    } else {
                                        Log.d("SCORE", "Failed to push data")
                                    }
                                }

                                override fun onFailure(call: Call<com.example.notzeroranger.highscore.HighScore>, t: Throwable) {
                                    Log.d("SCORE", "${t.message}")
                                }
                            })
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
}