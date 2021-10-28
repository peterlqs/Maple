package com.example.maple.ui.notification

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.maple.R
import com.example.maple.adapter.NotificationAdapter
import com.example.maple.data.NotificationDataSource
import com.example.maple.databinding.MainNotificationFragmentBinding

class MainNotification : Fragment() {

    companion object {
        fun newInstance() = MainNotification()
    }

    private lateinit var viewModel: MainNotificationViewModel

    //Binding
    private lateinit var _binding: MainNotificationFragmentBinding
    private val binding get() = _binding

    val dataSet = NotificationDataSource().loadNotifications()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.main_notification_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainNotificationViewModel::class.java)
        // TODO: Use the ViewModel

        val notiView = binding.notificationList
        notiView.apply {
            //Type grid, 2 item horizontally
            layoutManager = LinearLayoutManager(activity)
            //Set adapter, i cant explain the second parameter
            adapter = NotificationAdapter(this)

            notiView.setHasFixedSize(true)
        }
    }

}