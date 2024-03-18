package com.example.notzeroranger

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.example.notzeroranger.service.GameSound
import com.example.notzeroranger.service.ThemeSound
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // start theme music
        val themeSound = Intent(this, ThemeSound::class.java)
        startService(themeSound)

        val name_field: TextInputLayout = findViewById(R.id.nameLayout)
        var name_input: TextInputEditText = findViewById(R.id.name_input)
        // start game
        val playButton: Button = findViewById(R.id.playButton)
        playButton.setOnClickListener {
            name_field.isVisible = true
            if(name_input.text.toString()!="") {
                Player.name = name_input.text.toString()
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
            stopService(themeSound)
            val gameSound = Intent(this, GameSound::class.java)
            startService(gameSound) }
        }

        // move to settings
        val settingsButton: Button = findViewById(R.id.settingsButton)
        settingsButton.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        // quit game
        val quitButton: Button = findViewById(R.id.quitButton)
        quitButton.setOnClickListener {
            finishAffinity()
            System.exit(0)
        }
    }
}