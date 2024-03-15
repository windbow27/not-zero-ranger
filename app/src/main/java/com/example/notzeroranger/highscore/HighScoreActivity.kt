package com.example.notzeroranger.highscore

import com.example.notzeroranger.R
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notzeroranger.MainActivity
import com.example.notzeroranger.SettingsActivity
import com.example.notzeroranger.service.GameSound
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

class HighScoreActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.highscore_board_layout)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // move to settings
        val returnButton = findViewById<Button>(R.id.returnButton)
        returnButton.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        var highScoreList = ArrayList<HighScore>()
        // filler data
        highScoreList.add(HighScore("Duy", 10100))
        highScoreList.add(HighScore("Muy", 4500))
        highScoreList.add(HighScore("Huy", 7800))
        highScoreList.add(HighScore("Wind", 200))
        highScoreList.add(HighScore("Nghiax", 300))
        highScoreList.add(HighScore("Meow", 100))
        highScoreList.add(HighScore("Luan", 100))
        highScoreList.add(HighScore("Cuong", 0))
        //sort the list from top to low
        highScoreList.sortByDescending { it.score }
        try {
            val fos: FileOutputStream = openFileOutput("highScoreList", Context.MODE_PRIVATE)
            val oos = ObjectOutputStream(fos)
            oos.writeObject(highScoreList)
            oos.close()
            fos.close()
            val fis: FileInputStream = openFileInput("highScoreList")
            //since an object is being read,not just string, use Object input
            val ois = ObjectInputStream(fis)
            @Suppress("UNCHECKED_CAST")
            highScoreList = ois.readObject() as ArrayList<HighScore>
            ois.close()
            fis.close()
        } catch (e: IOException) {
            println(e.message)
        } catch (e: ClassNotFoundException) {
            println(e.message)
        }

        val customAdapter = HighScoreAdapter(highScoreList)
        //initiate recycler view
        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        recyclerView.adapter = customAdapter
        recyclerView.layoutManager = LinearLayoutManager(applicationContext)
    }
}