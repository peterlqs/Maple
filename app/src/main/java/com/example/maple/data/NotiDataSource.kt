package com.example.maple.data

import com.example.maple.R
import com.example.maple.data.NotificationObj

class NotificationDataSource {

    fun loadNotifications(): List<NotificationObj> {
        return listOf<NotificationObj>(
            NotificationObj(R.string.notification1),
            NotificationObj(R.string.notification2),
            NotificationObj(R.string.notification3),
            NotificationObj(R.string.notification4),
            NotificationObj(R.string.notification5),
            NotificationObj(R.string.notification6)
        )
    }
}