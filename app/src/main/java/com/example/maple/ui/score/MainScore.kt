package com.example.maple.ui.score

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.canhub.cropper.*
import com.example.maple.R
import com.example.maple.adapter.MainScoreAdapter
import com.example.maple.data.AverageSubject
import com.example.maple.data.SubjectData
import com.example.maple.data.WhichSubject
import com.example.maple.databinding.MainScoreFragmentBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class MainScore : Fragment(), WhichSubject {
    //ViewModel
    private val viewModel: ScoreViewModel by activityViewModels()

    //db
    private var db = FirebaseFirestore.getInstance()
    private val auth = Firebase.auth
    private val email = auth.currentUser?.uid.toString()

    //Binding
    private lateinit var _binding: MainScoreFragmentBinding
    private val binding get() = _binding

    //Initialize TAG for debug
    companion object {
        const val TAG = "MainScore"
    }

    //Set binding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MainScoreFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Fab button
        val fab = binding.fabAdd
        fab.setOnClickListener {
            val dialog = MainDialog()
            dialog.show(childFragmentManager, "customDialog")
        }
        // Get user desired score
        var wantedScore = 0.0
        db.collection("users").document(auth.uid!!).addSnapshotListener { value, error ->
            //Error handling
            if (error != null) {
                Log.w(MainScore.TAG, "listen:error", error)
                return@addSnapshotListener
            }
            if (value?.get("score") != null)
                wantedScore = value.get("score").toString().toDouble()
        }

        //Recycler view
        //First make a Firebase snapshot listener so whenever the data change
        //the recycler view change too
        val registration = db.collection("users")
            .document(email)
            .collection("subjectScores")
            .addSnapshotListener { value, e ->
                //Error handling
                if (e != null) {
                    Log.w(TAG, "listen:error", e)
                    return@addSnapshotListener
                }
                //List to contain scores when read from Firebase
                val listData = mutableListOf<SubjectData>()
                //Scan through each document in Firebase data
                for (doc in value!!.documents) {
                    //Each document value has to first change into Subject Data object to manipulate
                    val objectData = doc.toObject<SubjectData>()
                    //Error handler ( != null )
                    if (objectData != null) {
                        listData.add(objectData)
                    }
                }
                //Set to viewModel
                viewModel.setSubjectData(listData)

                //Calculating average score for each subject
                //This is complicated so maybe "the code just do stuff"
                val theScore = mutableListOf<Double>()
                val theSubject = mutableListOf<String>()
                val scannedSubject = mutableListOf<String>()
                val lowerThan8 = mutableListOf<String>()
                val scoreSubject = mutableListOf<AverageSubject>()
                //Total score,mul of all subject
                var totalScore = 0.0
                var totalMul = 0
                //a for loop to filter the same subject
                for (doc in listData) {
                    //get subject
                    val currentSubject = doc.sub
                    //if added to score then pass
                    if (doc.sub in scannedSubject) {
                        continue
                    }
                    //To start, each subject score and mul will be 0
                    var subjectScore = 0.0
                    var subjectMul = 0.0

                    //For each doc being scanned, if the subject match then append
                    for (docs in listData) {
                        if (docs.sub == currentSubject) {
                            subjectScore += docs.mul
                                ?.let { docs.score!!.times(it) }!!
                            subjectMul += docs.mul
                            //Calculate for total of all subjects
                            totalScore += docs.score!! * docs.mul
                            totalMul += docs.mul
                        }
                    }
                    //calculated average score
                    var finalScore =
                        "%.2f".format(subjectScore.div(subjectMul)).toDouble()
                    //No score = 0
                    if (finalScore.isNaN()) {
                        finalScore = 0.0
                    }
                    //Lower than 8 warning text
                    if (finalScore < wantedScore && finalScore != 0.0) {
                        lowerThan8.add("${currentSubject.toString()} ($finalScore)")
                    }
                    //Put into the list
                    if (currentSubject != null) {
                        scannedSubject.add(currentSubject)
                        theSubject.add(currentSubject)
                        theScore.add(finalScore)
                        //Add into list that has calculated average subject score
                        scoreSubject.add(AverageSubject(currentSubject, finalScore))
                    }
                }
                //Lower than 8 list, doesn't include 0
                val lowerScores = binding.txtLower
                val allLowerScore = lowerThan8.toString().replace("[", "").replace("]", "")
                if (allLowerScore.isNotEmpty()) lowerScores.text =
                    "Điểm dưới $wantedScore : $allLowerScore"
                //Average score of all subject
                val averageScoreAll = binding.txtAverage
                val averageScore = "%.2f".format(totalScore.div(totalMul)).toDouble()
                //If NaN then don't set
                if (!averageScore.isNaN()) averageScoreAll.text =
                    "Điểm trung bình : $averageScore"
                //Populate the recycler view
                val subjectView = binding.recyclerView
                subjectView.apply {
                    //Type grid, 2 item horizontally
                    layoutManager = GridLayoutManager(activity, 2)
                    //Set adapter, i cant explain the second parameter
                    adapter = MainScoreAdapter(scoreSubject, this@MainScore)
                }
            }

        // TODO DEV REMOVE LATER
        binding.button2.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_mainScore_to_mainExperiment)
        }
        binding.fabCam.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_mainScore_to_mainCamera)
        }
        //-------------------------


    }
    // This is to know which subject user press, idk how to explain
    override fun subjectName(subject: String) {
        //Leave blank
        viewModel.setWorkingSubject(subject)
    }
}