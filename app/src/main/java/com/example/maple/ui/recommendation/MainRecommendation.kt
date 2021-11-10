package com.example.maple.ui.recommendation

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.maple.adapter.RecommendationAdapter
import com.example.maple.data.AverageSubject
import com.example.maple.data.SubjectData
import com.example.maple.databinding.MainRecommendationFragmentBinding
import com.example.maple.hideKeyboard
import com.example.maple.ui.score.MainDialog
import com.example.maple.ui.score.MainScore
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class MainRecommendation : Fragment() {

    private lateinit var viewModel: MainRecommendationViewModel

    //Binding
    private lateinit var _binding: MainRecommendationFragmentBinding
    private val binding get() = _binding
    private lateinit var database: DatabaseReference
    private var db = FirebaseFirestore.getInstance()
    private val auth = Firebase.auth
    private val email = auth.currentUser?.uid.toString()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MainRecommendationFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set name
        db.collection("users").document(auth.uid!!).addSnapshotListener { value, error ->
            //Error handling
            if (error != null) {
                Log.w(MainScore.TAG, "listen:error", error)
                return@addSnapshotListener
            }
            binding.userName.text = value?.get("name").toString()
            if (value?.get("score") != null) {
                binding.userName2.text = "Mục tiêu điểm số : "
                binding.editTextTextPersonName3.setText(value.get("score").toString())
            }
        }
        // When hit enter button, setOnKeyListener is like onClickListener but for key
        binding.editTextTextPersonName3.setOnKeyListener { _, keyCode, event ->
            // If the key is enter AND user press on the enter key
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                val score = binding.editTextTextPersonName3.text.toString()
                if (score.matches("-?\\d+(\\.\\d+)?".toRegex())) {
                    db.collection("users").document(email).set(
                        hashMapOf("score" to score),
                        SetOptions.merge()
                    )
                } else {
                    Toast.makeText(context, "Vui lòng nhập số", Toast.LENGTH_SHORT).show()
                }
                hideKeyboard(view)
            }
            // If not enter then do nothing
            false
        }


        database = Firebase.database.reference

        database.child("subjects").child("12").get()
            .addOnSuccessListener {
                Log.e("Subjects", it.value.toString())
            }.addOnFailureListener {
                Log.e("Subjects", "Retrieval Failed")
            }

        val dummy_list = mutableListOf<String>("Toán", "Văn", "Anh")
        // (Nhi's note for variables)
        // avatar and fabChangeAva are used to change
        // avatar in the main menu
        val avatar = binding.userAva
        val fabChangeAva = binding.fabAddAva

        // (Nhi's justification for setOnClickListener)
        // This code block assists in changing avatar.
        // It is not completed right now due to the lack of graphic source.
        fabChangeAva.setOnClickListener {
            val dialog = MainDialog()
            dialog.show(childFragmentManager, "customDialog")

            //This part is for choosing avatar from our storage
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

        val registration = db.collection("users")
            .document(email)
            .collection("subjectScores")
            .addSnapshotListener { value, e ->
                //Error handling
                if (e != null) {
                    Log.w(MainScore.TAG, "listen:error", e)
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
                //Calculating average score for each subject
                //This is complicated so maybe "the code just do stuff"
                val theScore = mutableListOf<Double>()
                val theSubject = mutableListOf<String>()
                val scannedSubject = mutableListOf<String>()
                val lowerThan8 = mutableListOf<String>()
                val scoreSubject = mutableListOf<AverageSubject>()
                val lowerThan8Subjects = mutableListOf<AverageSubject>()
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
                        if ((finalScore <= wantedScore) and (finalScore > 0)) {
                            lowerThan8Subjects.add(AverageSubject(currentSubject, finalScore))
                        }
                    }
                    Log.e("Mark", lowerThan8Subjects.toString())

                    // (Nhi's justification for recyclerView)
                    // This code block is to use RecyclerView
                    // It is completed. Problems:
                    // - Being deprived of dataset
                    // - Not knowing to navigate
                    // - Not knowing how to display the screen in case of
                    // there is no subjects
                    val subjectView = binding.attentionSubject
                    subjectView.apply {
                        //Type grid, 2 item horizontally
                        layoutManager = LinearLayoutManager(activity)
                        //Set adapter, i cant explain the second parameter
                        adapter = RecommendationAdapter(lowerThan8Subjects)

                        subjectView.setHasFixedSize(true)
                    }
                }

            }

    }


    //Creating cardview for recommendation
    class Song {
        private var sName: String? = null

        constructor()
        constructor(name: String?) {
            sName = name
        }

        fun getName(): String? {
            return sName
        }

        fun setCode(name: String?) {
            sName = name
        }
    }

}