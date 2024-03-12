import android.content.Context
import android.graphics.Canvas
import com.example.notzeroranger.R
import com.example.notzeroranger.game.Bullet
import com.example.notzeroranger.game.PlayerBullet
import pl.droidsonroids.gif.GifDrawable

class Player(private val context: Context) {
    var x: Float = 0f
    var y: Float = 0f
    private var speed: Float = 20f
    private val hitbox: Float = 50f
    private val shootCooldown = 75

    private val screenWidth = context.resources.displayMetrics.widthPixels
    private val screenHeight = context.resources.displayMetrics.heightPixels
    private val playerDrawable: GifDrawable = GifDrawable(context.resources, R.drawable.player_gif)

    private var lastShootTime = System.currentTimeMillis()
    val bullets = mutableListOf<Bullet>()

    fun moveTo(targetX: Float, targetY: Float) {
        val dx = targetX - x
        val dy = targetY - y
        val mag = kotlin.math.sqrt(dx*dx + dy*dy)
        if (mag > 0) {
            val newX = x + speed * dx / mag
            val newY = y + speed * dy / mag

            // Check if the new coordinates are within the screen bounds
            if (newX >= 0 && newX <= screenWidth - hitbox) {
                x = newX
            }
            if (newY >= 0 && newY <= screenHeight - 2 * hitbox ) {
                y = newY
            }
        }
    }

    fun shoot() {
        val currentTime = System.currentTimeMillis()
        if (currentTime > lastShootTime + shootCooldown) {
            val bulletRight = PlayerBullet(context, x + hitbox / 5 * 4 - 10, y, Math.PI.toFloat() / 2) // right bullets
            val bulletLeft = PlayerBullet(context,x + hitbox / 5, y, Math.PI.toFloat() / 2) // left bullets
            bullets.add(bulletRight)
            bullets.add(bulletLeft)
            lastShootTime = currentTime
        }
    }

    fun update() {
        bullets.removeAll { it.isOffscreen(screenHeight) }
        bullets.forEach { it.update() }
    }

    fun draw(canvas: Canvas) {
        playerDrawable.setBounds(x.toInt(), y.toInt(), (x + hitbox).toInt(), (y + hitbox).toInt())
        playerDrawable.draw(canvas)

        bullets.forEach { it.draw(canvas) }
    }
}