package com.example.notzeroranger

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.notzeroranger.highscore.HighScoreActivity
import com.example.notzeroranger.setting.HealthManger

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

    class CustomArrayAdapter(context: Context, layout: Int, list: Array<String>, private val color: Int) : ArrayAdapter<String>(context, layout, list) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = super.getView(position, convertView, parent)
            val textView = view.findViewById<TextView>(android.R.id.text1)
            textView.setTextColor(color)
            return view
        }

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = super.getDropDownView(position, convertView, parent)
            val textView = view.findViewById<TextView>(android.R.id.text1)
            textView.setTextColor(color)
            textView.setBackgroundColor(ContextCompat.getColor(context, R.color.black))
            textView.textAlignment = View.TEXT_ALIGNMENT_CENTER
            return view
        }
    }

    private fun spinnerControl() {
        // data for the spinner
        val spinnerItems = arrayOf("1", "2", "3", "4", "5")

        // initialize the spinner
        val spinner: Spinner = findViewById(R.id.spinner)

        // create an ArrayAdapter using the string array and a default spinner layout
        val color = ContextCompat.getColor(this, R.color.white)
        val adapter = CustomArrayAdapter(this, android.R.layout.simple_spinner_item, spinnerItems, color)

        // specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // apply the adapter to the spinner
        spinner.adapter = adapter

        // set spinner selection by current health
        spinner.setSelection(HealthManger.getHealth().toInt()/10 -1)

        println("Current selection: " + HealthManger.getHealth().toInt()/10)
        println("Current health: " + HealthManger.getHealth())

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val health = parent?.getItemAtPosition(position).toString()
                println("Health: " + health)
                HealthManger.setHealth(health.toInt())
            }
            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
    }

    private fun soundControl() {
        val volumeSeekbar = findViewById<SeekBar>(R.id.soundBar)
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        volumeSeekbar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC))
        volumeSeekbar.progress = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)


        volumeSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })
    }
}