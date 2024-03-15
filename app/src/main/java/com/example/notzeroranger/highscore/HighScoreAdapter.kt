package com.example.notzeroranger.highscore
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.notzeroranger.R


class HighScoreAdapter(private val dataSet: ArrayList<HighScore>) : RecyclerView.Adapter<HighScoreAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView
        val score: TextView
        val rank: TextView

        init {
            // Define click listener for the ViewHolder's View
            name = view.findViewById(R.id.name)
            score = view.findViewById(R.id.score)
            rank = view.findViewById(R.id.rank)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.highscore_row_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.name.text = dataSet[position].name
        viewHolder.score.text = dataSet[position].score.toString() + " "
        viewHolder.rank.text = (position+1).toString()
        viewHolder.rank.setTextColor( when(position) {
            0 -> Color.parseColor("#ffffff")
            1 -> Color.parseColor("#fceeb6")
            2 -> Color.parseColor("#FB621B")
            else -> Color.parseColor("#49b393")
        })
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}