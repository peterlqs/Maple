package com.example.maple.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.maple.R
import com.example.maple.data.Notification

class NotificationAdapter(private val notifications: MutableList<Notification>) :
    RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val notiContent: TextView = itemView.findViewById(R.id.notification_content)
    }

    // Inflate UI
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_notification, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val time = notifications[position].time
        val content = notifications[position].content
        holder.notiContent.text = "Thời gian : $time\nNội dung : $content"
    }

    override fun getItemCount() = notifications.size
}

