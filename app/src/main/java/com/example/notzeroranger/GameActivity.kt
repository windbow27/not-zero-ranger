package com.example.notzeroranger

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.notzeroranger.game.GameView

class GameActivity : AppCompatActivity() {
    private lateinit var gameView: GameView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gameView = GameView(this)
        setContentView(gameView)
    }
}