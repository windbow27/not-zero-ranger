package com.example.notzeroranger.service

import android.media.MediaPlayer
import android.app.Service
import android.content.Context
import android.media.AudioManager

abstract class SoundService : Service(){
    protected var mediaPlayer: MediaPlayer? = null
//    protected var audioManager: AudioManager? =null
//
//    fun setVolume(volume: Float) {
//        mediaPlayer?.setVolume(volume, volume)
//    }

//    fun increaseVolume() {
//        audioManager?.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND)
//    }
//
//    fun decreaseVolume() {
//        audioManager?.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND)
//    }
}