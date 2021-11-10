package com.example.maple.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.maple.R
import com.example.maple.data.AverageSubject
import com.example.maple.ui.recommendation.MainRecommendationDirections

class RecommendationAdapter(private val subjects: MutableList<AverageSubject>) :
    RecyclerView.Adapter<RecommendationAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val subjectName: TextView = itemView.findViewById(R.id.subjectName)
    }

    // Inflate UI
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecommendationAdapter.ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.row_recommendation, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.subjectName.text = subjects[position].sub
        holder.subjectName.setOnClickListener {
            if (subjects[position].sub!!.lowercase() !in listOf(
                    "hóa học",
                    "lịch sử",
                    "vật lí",
                    "vật lý"
                )
            ) {
                Log.e("subjectName", "Hasn't added subject")
            } else if (subjects[position].sub != null) {
                val action =
                    MainRecommendationDirections.actionMainRecommendationToSubjectRecommend(subjects[position].sub.toString())
                Navigation.findNavController(holder.itemView).navigate(action)
            } else {
                Log.e("subjectName", "Not Found")
            }
        }
    }

    // Return subject list size
    override fun getItemCount() = subjects.size

}