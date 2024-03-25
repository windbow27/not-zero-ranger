package com.example.notzeroranger

import android.content.Intent
import android.os.Bundle
import android.webkit.WebView
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.notzeroranger.service.GameOverSound

class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        val webView: WebView = findViewById(R.id.webview)
        webView.loadUrl("https://github.com/windbow27/not-zero-ranger")

        // return to title
        val returnButton = findViewById<Button>(R.id.returnButton)
        returnButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}