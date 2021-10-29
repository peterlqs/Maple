package com.example.maple.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.maple.R

class NotificationAdapter(val notifications: RecyclerView) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val notiContent: TextView = itemView.findViewById(R.id.notificationList)
    }

    // Inflate UI
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationAdapter.ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.row_notification, parent, false)
        return NotificationAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentNoti = notifications[position]
        holder.notiContent.text = "$currentNoti"
    }

    override fun getItemCount() = notifications.size
}

