package com.example.notzeroranger

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.SeekBar
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.example.notzeroranger.highscore.HighScoreActivity

class SettingsActivity : AppCompatActivity() {
    private lateinit var audioManager: AudioManager

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // go to high score
        val highScoreButton = findViewById<Button>(R.id.highScore)
        highScoreButton.setOnClickListener {
            val intent = Intent(this, HighScoreActivity::class.java)
            startActivity(intent)
        }

        // return to title
        val returnButton = findViewById<Button>(R.id.returnButton)
        returnButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        spinnerControl()
        soundControl()
    }

    fun spinnerControl() {
        // Data for the spinner
        val spinnerItems = arrayOf("1", "2", "3", "4", "5")

        // Initialize the spinner
        val spinner: Spinner = findViewById(R.id.spinner)

        // Create an ArrayAdapter using the string array and a default spinner layout
        val adapter = ArrayAdapter(this,
            android.R.layout.simple_spinner_item, spinnerItems)

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Apply the adapter to the spinner
        spinner.adapter = adapter

        // Optional: Set a listener to respond to spinner item selections
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                //
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }
    }

    fun soundControl() {
        val volumeSeekbar = findViewById<SeekBar>(R.id.soundBar)
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager;
        volumeSeekbar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        volumeSeekbar.progress = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);


        volumeSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })
    }
}