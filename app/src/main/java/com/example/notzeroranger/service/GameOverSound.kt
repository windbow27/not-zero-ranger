package com.example.notzeroranger.service

import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import com.example.notzeroranger.R

class GameOverSound : SoundService() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        // Create MediaPlayer and start playing the sound
        mediaPlayer = MediaPlayer.create(this, R.raw.gameover)
        mediaPlayer?.isLooping = true // Set looping to true for continuous play
        mediaPlayer?.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Release the MediaPlayer when the service is destroyed
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}