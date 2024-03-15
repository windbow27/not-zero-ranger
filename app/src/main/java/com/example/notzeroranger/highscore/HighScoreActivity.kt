package com.example.notzeroranger.highscore

import com.example.notzeroranger.R
import android.content.Context
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

        // add array for high score reading and writing from storage
        var highscoreList = ArrayList<HighScore>()
        // some random highscore so the view wouldnt be empty when app start (like me)
        highscoreList.add(HighScore("Duy", 101))
        highscoreList.add(HighScore("Muy", 45))
        highscoreList.add(HighScore("Huy", 78))
        highscoreList.add(HighScore("Wind", 2))
        highscoreList.add(HighScore("Nghiax", 3))
        highscoreList.add(HighScore("Meow", 100))
        highscoreList.add(HighScore("Luan", 1))
        highscoreList.add(HighScore("Cuong", 0))
        //sort the list from top to low
        highscoreList.sortByDescending { it.score }
        try {
            //open file output to write into internal storage
            val fos: FileOutputStream = openFileOutput("highscoreList", Context.MODE_PRIVATE)
            //since an object is being writen,not just string, use Object output
            val oos: ObjectOutputStream = ObjectOutputStream(fos)
            oos.writeObject(highscoreList)
            //After writing, close all streams
            oos.close()
            fos.close()
            //open file input to read from internal storage
            val fis: FileInputStream = openFileInput("highscoreList")
            //since an object is being read,not just string, use Object input
            val ois: ObjectInputStream = ObjectInputStream(fis)
            //cast the object just read into array list
            highscoreList = ois.readObject() as ArrayList<HighScore>
            //After reading, close all streams
            ois.close()
            fis.close()
        } catch (e: IOException) {
            println(e.message)
        } catch (e: ClassNotFoundException) {
            println(e.message)
        }
        //put list into adapter
        val customAdapter = HighScoreAdapter(highscoreList)
        //initiate recycler view
        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        recyclerView.adapter = customAdapter
        recyclerView.layoutManager = LinearLayoutManager(applicationContext)
    }

    //exit the activity function
    fun onBackButtonClicked() {
        finish()
    }
}