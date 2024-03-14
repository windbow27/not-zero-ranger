package com.example.notzeroranger

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.notzeroranger.service.GameSound

class GameOverActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gameover)

        // retry
        val retryButton = findViewById<Button>(R.id.retryButton)
        retryButton.setOnClickListener {
            stopService(Intent(this, GameSound::class.java))
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
            startService(Intent(this, GameSound::class.java))
        }

        // return to title
        val returnButton = findViewById<Button>(R.id.returnButton)
        returnButton.setOnClickListener {
            stopService(Intent(this, GameSound::class.java))
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}