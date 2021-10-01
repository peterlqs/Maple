package com.example.maple.ui.score

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import com.example.maple.R
import com.example.maple.data.SubjectData
import com.example.maple.getCurrentDateTime
import com.example.maple.toString
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class SecondDialog : DialogFragment() {
    //ViewModel
    private val viewModel: ScoreViewModel by activityViewModels()

    //db
    private var db = FirebaseFirestore.getInstance()
    private val auth = Firebase.auth
    private val email = auth.currentUser?.email.toString()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView: View = inflater.inflate(R.layout.second_dialog_fragment, container, false)
        //Set initial bg invisible
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        //Cancel btn
        val cancelButton = rootView.findViewById<FloatingActionButton>(R.id.fabDelete)
        cancelButton.setOnClickListener {
            dismiss()
        }
        //Submit btn
        val submitButton = rootView.findViewById<Button>(R.id.btnSubmit)
        submitButton.setOnClickListener {
            //The input info
            val score = rootView.findViewById<EditText>(R.id.editText2).text.toString()//.toDouble()
            val mul = rootView.findViewById<EditText>(R.id.editText3).text.toString()//.toInt()
            val month = rootView.findViewById<EditText>(R.id.editText4).text.toString()//.toInt()

            //Empty check and month check
            if (score.toString().trim().isNotEmpty() && month.toString().trim().isNotEmpty()
                && mul.toString().isNotEmpty() && month.toInt() < 13 && month.toInt() > 0
            ) {
                //initialize db
                updateData(
                    score.toDouble(),
                    month.toInt(),
                    mul.toInt(),
                    viewModel.workingSubject,
                )
                Toast.makeText(context, "Thành công", Toast.LENGTH_SHORT).show()
            } else if (month.toInt() > 12 || month.toInt() < 1) {
                Toast.makeText(context, "Bạn cần  tháng từ 1 tới 12", Toast.LENGTH_SHORT).show()
            } else if (mul.toInt() < 1 || mul.toInt() > 3) {
                Toast.makeText(context, "Bạn cần hệ số từ 1 đến 3", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Bạn nhập thiếu", Toast.LENGTH_SHORT).show()
            }
        }
        return rootView
    }

    //Push data to Firebase
    private fun updateData(
        score: Double,
        month: Int,
        selectedChipValue: Int,
        workingSubject: LiveData<String>,
    ) {
        //Set document id to time so first of all get the time
        var timeSet = "$month ${getCurrentDateTime().toString("yyyyMMdd HH:mm:ss")}"

        // If month is lower then 10 then add 0
        if (month < 10) {
            timeSet = "0${month} ${getCurrentDateTime().toString("yyyyMMdd HH:mm:ss")}"
        }

        //Add new data to viewModel
        val subjectData = viewModel.subjectData.value
        subjectData!!.add(
            SubjectData(
                month,
                selectedChipValue,
                score,
                workingSubject.value.toString()
            )
        )
        viewModel.setSubjectData(subjectData)

        // Add data to Firebase, you can set Object to firebase btw
        db.collection("users")
            .document(email)
            .collection("subjectScores")
            .document(timeSet)
            .set(
                SubjectData(
                    month,
                    selectedChipValue,
                    score,
                    workingSubject.value.toString()
                )
            )
            .addOnSuccessListener {
                Log.d("Something", "DocumentSnapshot successfully written!")
            }
            .addOnFailureListener { e -> Log.w("Something", "Error writing document", e) }
    }
}