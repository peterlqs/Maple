package com.example.maple.ui.score

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.maple.R
import com.example.maple.data.SubjectData
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class MainDialog : DialogFragment() {
    //ViewModel
    private val viewModel: ScoreViewModel by activityViewModels()

    //db
    private var db = FirebaseFirestore.getInstance()
    private val auth = Firebase.auth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView: View = inflater.inflate(R.layout.main_dialog_fragment, container, false)
        //Set initial bg invisible
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        //The text view
        val subject = rootView.findViewById<TextInputEditText>(R.id.editText)
        //Cancel btn
        val cancelButton = rootView.findViewById<FloatingActionButton>(R.id.fabDelete)
        cancelButton.setOnClickListener {
            dismiss()
        }
        //Submit btn
        val submitBtn = rootView.findViewById<MaterialButton>(R.id.btnSubmit)
        submitBtn.setOnClickListener {
            //If input is correct add to firebase then clear the input so user can type another one
            //Will not close keyboard in case user want to write many subjects at once
            if (subject.text.toString().trim().isNotBlank()) {
                addSubject(subject.text.toString())
                subject.text?.clear()
            } else Toast.makeText(context, "Bạn hãy nhập lại", Toast.LENGTH_SHORT).show()
        }
        //Clear button
        val clearBtn = rootView.findViewById<MaterialButton>(R.id.btnDelete)
        clearBtn.setOnClickListener {
            subject.text?.clear()
        }
        return rootView
    }

    //Add subject on firebase
    private fun addSubject(subject: String) {
        //Create a complete subject ( have to set to 0 cuz it doesn't accept null/blank )
        val subjectName = hashMapOf(
            "month" to 0,
            "sub" to subject,
            "score" to 0,
            "mul" to 0
        )
        //Update data on firebase
        db.collection("users")
            .document(auth.currentUser?.email.toString())
            .collection("subjectScores")
            .add(subjectName).addOnSuccessListener {
                //Get data from viewModel, add new value then set new value to viewModel
                val originalData = viewModel.subjectData.value
                originalData?.add(
                    SubjectData(
                        0,
                        0,
                        0.0,
                        subject
                    )
                )
                viewModel.setSubjectData(originalData!!)
                Log.d("Something", "DocumentSnapshot successfully written!")
                Toast.makeText(context, "Thêm môn thành công", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener { e ->
                Log.w(
                    "Something",
                    "Error writing document",
                    e
                )
            }
    }
}