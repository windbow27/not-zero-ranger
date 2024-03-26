package com.example.notzeroranger.highscore

import android.annotation.SuppressLint
import com.example.notzeroranger.R
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notzeroranger.SettingsActivity
import com.example.notzeroranger.database.DemoDbHeper
import com.example.notzeroranger.service.PlayerScore
import com.example.notzeroranger.service.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream


class HighScoreActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
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
        val dbHelper = DemoDbHeper(this)
        val db = dbHelper.readableDatabase
        val projection = arrayOf("name", "score")
        var cursor:Cursor = db.query(com.example.notzeroranger.database.HighScore.PlayerEntry.TABLE_NAME,
            projection,null,null,null,null,"score")
        try {
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex("name")
                val scoreIndex = cursor.getColumnIndex("score")
                do {
                    val name = cursor.getString(nameIndex)
                    val score = cursor.getString(scoreIndex)
                    highScoreList.add(HighScore(name, score.toLong()))
                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {}
        finally {
            cursor.close()
            db.close()
        }
        // filler data


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

        // update local score view
        var customAdapter = HighScoreAdapter(highScoreList)
        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        recyclerView.adapter = customAdapter
        recyclerView.layoutManager = LinearLayoutManager(applicationContext)

        // push local score into remote database
        highScoreList.forEach {
            RetrofitInstance.api.pushData(it).enqueue(object : Callback<HighScore> {
                override fun onResponse(call: Call<HighScore>, response: Response<HighScore>) {
                    if (response.isSuccessful) {
                        val data = response.body()
                        Log.d("SCORE", "Pushed data successfully: ${data.toString()}")
                    } else {
                        Log.d("SCORE", "Failed to push data")
                    }
                }

                override fun onFailure(call: Call<HighScore>, t: Throwable) {
                    Log.d("SCORE", "${t.message}")
                }
            })
        }

        // reupdate local score view when clicked
        val local: Button = findViewById(R.id.local)
        local.setOnClickListener {
            customAdapter = HighScoreAdapter(highScoreList)
            recyclerView.adapter = customAdapter
            recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        }

        // get score from remote database
        val global: Button = findViewById(R.id.global)
        var globalHighScore = ArrayList<HighScore>()
        global.setOnClickListener {
            RetrofitInstance.api.getData(10, "score.desc").enqueue(object : Callback<ArrayList<HighScore>> {
                override fun onResponse(call: Call<ArrayList<HighScore>>, response: Response<ArrayList<HighScore>>) {
                    if (response.isSuccessful) {
                        val data = response.body()
                        Log.d("RESPONSE", "Score: ${data.toString()}")
                        if (data != null) {
                            globalHighScore = data
                        }
                    } else {
                        Log.d("SCORE", "Failed to get data")
                    }
                }

                override fun onFailure(call: Call<ArrayList<HighScore>>, t: Throwable) {
                    Log.d("SCORE", "${t.message}")
                }
            })

            // update global score view
            customAdapter = HighScoreAdapter(globalHighScore)
            recyclerView.adapter = customAdapter
            recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        }
    }
}