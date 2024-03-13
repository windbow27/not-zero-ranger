package com.example.notzeroranger.service

import android.media.MediaPlayer
import android.app.Service


abstract class SoundService : Service(){
    protected var mediaPlayer: MediaPlayer? = null
}