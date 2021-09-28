package com.example.maple.ui.score

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.maple.adapter.MainScoreAdapter
import com.example.maple.data.AverageSubject
import com.example.maple.data.SubjectData
import com.example.maple.databinding.MainScoreFragmentBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class MainScore : Fragment() {
    //ViewModel
    private val viewModel: ScoreViewModel by activityViewModels()

    //db
    private var db = FirebaseFirestore.getInstance()
    private val auth = Firebase.auth
    private val email = auth.currentUser?.email.toString()

    //Binding
    private lateinit var _binding: MainScoreFragmentBinding
    private val binding get() = _binding

    companion object {
        const val TAG = "MainScore"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = MainScoreFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Fab button
        val fab = binding.addMain
        fab.setOnClickListener {
            val dialog = MainDialog()
            dialog.show(childFragmentManager, "customDialog")
        }

        //Recycler view
        val registration = db.collection("users")
            .document(email)
            .collection("subjectScores")
            .addSnapshotListener { value, e ->
                //Error handling
                if (e != null) {
                    Log.w(TAG, "listen:error", e)
                    return@addSnapshotListener
                }
                //List to contain scores
                val listData = mutableListOf<SubjectData>()
                //Scan through each doc
                for (doc in value!!.documents) {
                    Log.d(TAG, doc.toString())
//                    Log.d(TAG, doc.toString())
                    val objectData = doc.toObject<SubjectData>()
                    if (objectData != null) {
                        listData.add(objectData)
                    }
                }
                viewModel.setSubjectData(listData)

                //Calculating average score for each subject
                val theScore = mutableListOf<Double>()
                val theSubject = mutableListOf<String>()
                val scannedSubject = mutableListOf<String>()
                val lowerThan8 = mutableListOf<String>()
                val scoreSubject = mutableListOf<AverageSubject>()
                for (doc in listData) {
                    //a for loop to filter the same subject
                    val currentSubject = doc.sub
                    //if added to score then pass
                    if (doc.sub in scannedSubject) {
                        continue
                    }
                    var subjectScore = 0.0
                    var subjectMul = 0.0
                    for (docs in listData) {
                        if (docs.sub == currentSubject) {
                            subjectScore += docs.mul
                                ?.let { docs.score!!.times(it) }!!
                            subjectMul += docs.mul
                        }
                    }
                    var finalScore =
                        "%.2f".format(subjectScore.div(subjectMul)).toDouble()
                    //No score = 0
                    if (finalScore.isNaN()) {
                        finalScore = 0.0
                    }
                    //Lower than 8 warning text
                    if (finalScore < 8) {
                        lowerThan8.add("${currentSubject.toString()} ($finalScore)")
                    }
                    //put into the list
                    if (currentSubject != null) {
                        scannedSubject.add(currentSubject)
                        scoreSubject.add(AverageSubject(currentSubject, finalScore))
                        theSubject.add(currentSubject)
                        theScore.add(finalScore)
                    }
                }

                val subjectView = binding.recyclerView
                subjectView.apply {
                    layoutManager = GridLayoutManager(activity, 2)
                    adapter =
                        MainScoreAdapter(scoreSubject)
                }
            }


    }

    private fun getScore() {
        val listData = mutableListOf<SubjectData>()
        db.collection("users")
            .document(email)
            .collection("subjectScores")
            .get()
            .addOnCompleteListener { documents ->
                if (documents.isSuccessful) {
                    for (doc in documents.result) {
                        val objectData = doc.toObject<SubjectData>()
                        listData.add(objectData)
                    }
                    viewModel.setSubjectData(listData)
                }
            }
    }
}