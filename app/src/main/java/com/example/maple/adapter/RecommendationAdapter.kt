package com.example.maple.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.maple.R

class RecommendationAdapter(val subjects: MutableList<String>) : RecyclerView.Adapter<RecommendationAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val subjectName: Button = itemView.findViewById(R.id.subjectName)
    }

    // Inflate UI
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecommendationAdapter.ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.row_recommendation, parent, false)
        return RecommendationAdapter.ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentSubject = subjects[position]
        holder.subjectName.text = "$currentSubject"
        holder.subjectName.setOnClickListener {
            //Click to jump to detail recommendation
        }
    }

    // Return subject list size
    override fun getItemCount() = subjects.size

}