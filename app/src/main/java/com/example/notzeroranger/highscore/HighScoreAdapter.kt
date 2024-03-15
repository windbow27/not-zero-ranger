package com.example.notzeroranger.highscore
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.notzeroranger.R


class HighScoreAdapter(private val dataSet: ArrayList<HighScore>) : RecyclerView.Adapter<HighScoreAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView
        val score: TextView
        val rank: TextView

        init {
            // define click listener
            name = view.findViewById(R.id.name)
            score = view.findViewById(R.id.score)
            rank = view.findViewById(R.id.rank)
        }
    }

    // create new views
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.highscore_row_item, viewGroup, false)

        return ViewHolder(view)
    }

    // replace the contents of a view
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        dataSet[position].let { highScore ->
            viewHolder.name.text = highScore.name
            viewHolder.score.text = viewHolder.itemView.context.getString(R.string.score_text, highScore.score.toString())
            viewHolder.rank.text = (position + 1).toString()
            viewHolder.rank.setTextColor(
                when (position) {
                    0 -> Color.parseColor("#FFD700")
                    1 -> Color.parseColor("#C0C0C0")
                    2 -> Color.parseColor("#FB621B")
                    else -> Color.parseColor("#B87333")
                }
            )
        }
    }

    // Return the size of dataset
    override fun getItemCount() = dataSet.size

}