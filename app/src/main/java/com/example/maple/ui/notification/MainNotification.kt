package com.example.maple.ui.notification

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.maple.adapter.NotificationAdapter
import com.example.maple.data.Notification
import com.example.maple.data.NotificationDataSource
import com.example.maple.databinding.MainNotificationFragmentBinding
import com.example.maple.ui.score.MainScore
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class MainNotification : Fragment() {

    companion object {
        fun newInstance() = MainNotification()
    }

    //db
    private var db = FirebaseFirestore.getInstance()
    private val auth = Firebase.auth
    private val email = auth.currentUser?.uid.toString()

    private lateinit var viewModel: MainNotificationViewModel

    //Binding
    private lateinit var _binding: MainNotificationFragmentBinding
    private val binding get() = _binding

    val dataSet = NotificationDataSource().loadNotifications()

    //Set binding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MainNotificationFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //the recycler view change too
        val registration = db.collection("notifications")
            .addSnapshotListener { value, e ->
                //Error handling
                if (e != null) {
                    Log.w(MainScore.TAG, "listen:error", e)
                    return@addSnapshotListener
                }
                val news = mutableListOf<Notification>()
                for (doc in value!!.documents) {
                    //Each document value has to first change into Subject Data object to manipulate
                    val objectData = Notification(
                        doc.get("date").toString(),
                        doc.getString("content").toString()
                    )
                    //Error handler ( != null )
                    news.add(objectData)
                }
                //Populate the recycler view
                val subjectView = binding.notificationList
                subjectView.apply {
                    //Type grid, 2 item horizontally
                    layoutManager = LinearLayoutManager(activity)
                    //Set adapter, i cant explain the second parameter
                    adapter = NotificationAdapter(news)
                }

            }

    }

}