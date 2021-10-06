package com.example.maple.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.maple.R
import com.example.maple.data.AverageSubject
import com.example.maple.data.WhichSubject

class MainScoreAdapter(
//    val theSubject: MutableList<String>,
//    val theScore: MutableList<Double>,
    private val subjectScore: MutableList<AverageSubject>,
    // The listener here is like an interface to communicate between recycler and fragment
    private val listener: WhichSubject
) :
    RecyclerView.Adapter<MainScoreAdapter.ViewHolder>() {


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Get button's view
        val subject: Button = itemView.findViewById(R.id.subject)
    }

    // Inflate UI
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_main_score, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Get average score
        val currentScore = subjectScore[position].score
        // Get current subject ( ex : 9.4, Chemistry )
        val currentSubject = subjectScore[position].sub
        // Pass info to the TextView
        holder.subject.text = "$currentSubject\n$currentScore"
        // When click on a subject
        holder.subject.setOnClickListener {
            Log.d("adapter", currentSubject!!)
            // Pass which subject was clicked on to the next fragment
            listener.subjectName(currentSubject)
            // Navigate to the next fragment
            holder.itemView.findNavController().navigate(R.id.action_mainScore_to_secondScore)
        }
    }

    // Return list size
    override fun getItemCount(): Int {
        return subjectScore.size
    }
}