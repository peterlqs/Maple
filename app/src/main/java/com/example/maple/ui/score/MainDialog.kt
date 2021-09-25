package com.example.maple.ui.score

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.maple.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore

class MainDialog : DialogFragment() {
    //ViewModel
    private val viewModel: MainScoreViewModel by activityViewModels()

    //db
    private var db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView: View = inflater.inflate(R.layout.dialog_main_score, container, false)
        //Set initial bg invisible
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        //Cancel btn
        val cancelButton = rootView.findViewById<FloatingActionButton>(R.id.fabDelete)
        cancelButton.setOnClickListener {
            dismiss()
        }
        return rootView
    }

}