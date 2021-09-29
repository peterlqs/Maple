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
    private val listener: WhichSubject
) :
    RecyclerView.Adapter<MainScoreAdapter.ViewHolder>() {


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val subject: Button = itemView.findViewById(R.id.subject)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_main_score, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentScore = subjectScore[position].score
        val currentSubject = subjectScore[position].sub
        holder.subject.text = "$currentSubject\n$currentScore"
        holder.subject.setOnClickListener {
            Log.d("adapter", currentSubject!!)
            listener.subjectName(currentSubject)
            holder.itemView.findNavController().navigate(R.id.action_mainScore_to_secondScore)
        }
    }

    override fun getItemCount(): Int {
        return subjectScore.size
    }
}