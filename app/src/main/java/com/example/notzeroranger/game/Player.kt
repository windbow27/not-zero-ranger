import android.content.Context
import android.graphics.Canvas
import com.example.notzeroranger.R
import com.example.notzeroranger.game.Bullet
import com.example.notzeroranger.game.Enemy
import com.example.notzeroranger.game.GameObject
import com.example.notzeroranger.game.PlayerBullet
import pl.droidsonroids.gif.GifDrawable

class Player(private val context: Context, x: Float, y: Float, width: Float, height: Float): GameObject(x, y, width, height) {
    private var points: Int = 0
    private val shootCooldown = 75
    private val screenWidth = context.resources.displayMetrics.widthPixels
    private val screenHeight = context.resources.displayMetrics.heightPixels
    private val playerDrawable: GifDrawable = GifDrawable(context.resources, R.drawable.player_gif)
    private var lastShootTime = System.currentTimeMillis()
    override var bullets = mutableListOf<Bullet>()
    override var health: Float = 30f
    override var speed: Float = 20f

    fun addPoints(points: Int) {
        this.points += points
    }

    fun getPoints(): Int {
        return points
    }

    fun moveTo(targetX: Float, targetY: Float) {
        val dx = targetX - x
        val dy = targetY - y
        val mag = kotlin.math.sqrt(dx*dx + dy*dy)
        if (mag > 0) {
            val newX = x + speed * dx / mag
            val newY = y + speed * dy / mag

            // Check if the new coordinates are within the screen bounds
            if (newX >= 0 && newX <= screenWidth - width) {
                x = newX
            }
            if (newY >= 0 && newY <= screenHeight - 2 * height ) {
                y = newY
            }
        }
    }

    fun shoot() {
        val currentTime = System.currentTimeMillis()
        if (currentTime > lastShootTime + shootCooldown) {
            val bulletRight = PlayerBullet(context, x + width / 5 * 4 - 10, y) // right bullets
            val bulletLeft = PlayerBullet(context,x + height / 5, y) // left bullets
            bullets.add(bulletRight)
            bullets.add(bulletLeft)
            lastShootTime = currentTime
        }
    }

    fun update() {
        bullets.removeAll { it.isOffscreen(screenHeight, screenWidth) }
        bullets.forEach { it.update() }
    }

    fun draw(canvas: Canvas) {
        playerDrawable.setBounds(x.toInt(), y.toInt(), (x + width).toInt(), (y + height).toInt())
        playerDrawable.draw(canvas)

        bullets.forEach { it.draw(canvas) }
    }
}