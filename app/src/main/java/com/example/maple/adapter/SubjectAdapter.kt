package com.example.maple.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.maple.R
import com.example.maple.data.SubjectInfo

class SubjectAdapter(private val practice: MutableList<SubjectInfo>) :
    RecyclerView.Adapter<SubjectAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val chapter: TextView = itemView.findViewById(R.id.Exercise)
        val link: TextView = itemView.findViewById(R.id.hyperlink)
    }

    // Inflate UI
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectAdapter.ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.row_practice, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (practice[position].section == "name") {
            holder.chapter.text = practice[position].link
            holder.link.text = "Những video dưới thuộc chương trên"
        } else {
            holder.chapter.text = practice[position].section
            holder.link.text = practice[position].link
            Log.d("SLKD", practice[position].link.toString())
        }
    }

    // Return subject list size
    override fun getItemCount() = practice.size

}