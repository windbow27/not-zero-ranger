package com.example.notzeroranger

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.notzeroranger.game.GameView

class GameActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val gameView = GameView(this)
        setContentView(gameView)
    }
}