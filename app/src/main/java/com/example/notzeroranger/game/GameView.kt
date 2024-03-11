package com.example.notzeroranger.game

import android.content.Context
import android.view.SurfaceHolder
import android.view.SurfaceView

class GameView(context: Context) : SurfaceView(context), SurfaceHolder.Callback {
    init {
        holder.addCallback(this)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        // This is called immediately after the surface is first created.
        // Implement your game setup here.
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        // This is called immediately after any structural changes
        // (format or size) have been made to the surface.
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        // This is called immediately before a surface is being destroyed.
        // Implement your cleanup here.
    }
}