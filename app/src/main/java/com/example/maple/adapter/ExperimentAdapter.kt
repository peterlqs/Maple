package com.example.maple.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.maple.R
import com.example.maple.data.Task

class ExperimentAdapter(
    private val allTask: MutableList<Task>
) :
    RecyclerView.Adapter<ExperimentAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Get button's view
        val time: TextView = itemView.findViewById(R.id.scoreHere)
        val dayText: TextView = itemView.findViewById(R.id.dayText)
    }

    // Inflate UI
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_experiment, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.time.text = allTask[position].date
        holder.dayText.text = allTask[position].task
    }

    // Return list size
    override fun getItemCount(): Int {
        return allTask.size
    }
}