package com.example.maple.ui.score

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.maple.R
import com.example.maple.adapter.SecondScoreAdapter
import com.example.maple.data.Diem
import com.example.maple.data.SubjectData
import com.example.maple.databinding.SecondScoreFragmentBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase


class SecondScore : Fragment() {
    //ViewModel
    private val viewModel: ScoreViewModel by activityViewModels()

    //db
    private var db = FirebaseFirestore.getInstance()
    private val auth = Firebase.auth
    private val email = auth.currentUser?.email.toString()

    //Binding
    private lateinit var _binding: SecondScoreFragmentBinding
    private val binding get() = _binding

    //Initialize TAG for debug
    companion object {
        const val TAG = "SecondScore"
    }

    //Set binding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SecondScoreFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Load listener
        loadListener()
        //Delete subject btn
        val currentSubject = viewModel.workingSubject.value.toString()
        val btnDelete = binding.btnDelete
        btnDelete.setOnClickListener {
            deleteSubject(email, currentSubject)
        }
        //Set subject name
        binding.subjectName.text = viewModel.workingSubject.value
        //If press Fab ( Floating action button ) show the score input dialog
        binding.fabAdd.setOnClickListener {
            val dialog = SecondDialog()
//            SecondDialog is the score input dialog
            //idk what childFragmentManager is, tag is required but it can be anything
            dialog.show(childFragmentManager, "customDialog")
        }
        //Set the 3 toggle recycler view, can someone fix this DRY code
        //If press on the rectangle open/close button then it open/close
        binding.down1.setOnClickListener {
            //If currently open then close and vice versa
            //Visibility to check if it's visible or not then open/close
            //The same goes for the belows
            if (binding.recyclerView1.visibility == View.GONE) {
                binding.recyclerView1.visibility = View.VISIBLE
            } else binding.recyclerView1.visibility = View.GONE
        }
        binding.down2.setOnClickListener {
            if (binding.recyclerView2.visibility == View.GONE) {
                binding.recyclerView2.visibility = View.VISIBLE
            } else binding.recyclerView2.visibility = View.GONE
        }
        binding.down3.setOnClickListener {
            if (binding.recyclerView3.visibility == View.GONE) {
                binding.recyclerView3.visibility = View.VISIBLE
            } else binding.recyclerView3.visibility = View.GONE
        }
    }

    private fun loadListener() {
        // When click on a subject it change to this fragment, so we have to know which subject
        // was clicked on, hence call currentSubject to know
        val currentSubject = viewModel.workingSubject.value.toString()

        // TODO detach listener when go to another fragment
        // Attach listener means if the data change then it refresh the UI
        // but when first attached it will load the current data into the UI
        val registration = db.collection("users")
            .document(email)
            .collection("subjectScores")
            .addSnapshotListener { value, e ->
                //Error handling
                if (e != null) {
                    Log.w(MainScore.TAG, "listen:error", e)
                    return@addSnapshotListener
                }
                // 3 lists to store Score that has mul of 1 or 2 or 3
                // I cant rly explain all the stuff below so u dun need to understand all
                val mul1List = mutableListOf<Diem>()
                val mul2List = mutableListOf<Diem>()
                val mul3List = mutableListOf<Diem>()
                val listId = mutableListOf<String>()

                //Average score
                var totalMul = 0
                var totalScore = 0.0
//                var id = ""
                //List to contain scores when read from Firebase
                val listData = mutableListOf<SubjectData>()
                //Scan through each document in Firebase data
                for (doc in value!!.documents) {
                    //Each document value has to first change into Subject Data object to manipulate
                    val objectData = doc.toObject<SubjectData>()
                    //Error handler ( != null )
                    if (objectData != null) {
                        listData.add(objectData)
                        listId.add(doc.id)
                    }
                }
                // The count is to get the id cuz i forgot to include it in the Diem object
                var count = 0
                for (doc in listData) {
                    //If not current subject and not month 0
                    if (doc.sub != currentSubject || doc.month
                            ?.toInt() == 0
                    ) continue
                    //Add average score
                    //If added to score then pass
                    val currentMul = doc.mul
                    val currentScore = doc.score
                    val currentMonth = doc.month?.toDouble()
                    val currentID = listId[count].trim()
                    //Null check basically
                    if (currentScore != null && currentMonth != null && currentMul != null) {
                        // Calculate score normally
                        totalMul += currentMul
                        totalScore += currentScore * currentMul

                        //If mul 1 then build recycler view 1 if 2 then build...
                        when (currentMul) {
                            1 -> {
                                mul1List.add(Diem(currentScore, currentMonth, currentID))
                            }
                            2 -> {
                                mul2List.add(Diem(currentScore, currentMonth, currentID))
                            }
                            else -> {
                                mul3List.add(Diem(currentScore, currentMonth, currentID))
                            }
                        }
                    }
                    count += 1
                }
                //Put average score into text
                val finalScore =
                    "%.2f".format(totalScore.div(totalMul)).toDouble()
                binding.DTB.text = finalScore.toString()
                //replace nan with 0
                if (finalScore.isNaN()) binding.DTB.text = "0"
                Log.e("error", finalScore.toString())
                //Create 3 different recyclerView
                buildRecyclerView(mul1List, 1)
                buildRecyclerView(mul2List, 2)
                buildRecyclerView(mul3List, 3)
            }
    }

    //Delete subject
    private fun deleteSubject(email: String, currentSub: String) {
        //Get current subject data
        var subjectData = viewModel.subjectData.value
        //The below line is to remove all the score that match the subject in viewModel ( local data )
        subjectData = subjectData!!.filter { it.sub != currentSub }.toMutableList()
        // Once done filter set the data in viewModel
        viewModel.setSubjectData(subjectData)
        //Delete on Firebase
        db.collection("users/")
            .document(email)
            .collection("subjectScores")
            .addSnapshotListener(EventListener { documentSnapshots, e ->
                //Null check
                if (e != null) {
                    Log.e("TAG", "Listen failed", e)
                    return@EventListener
                }
                if (documentSnapshots != null) {
                    // Scan every document if match subject then delete
                    for (i in documentSnapshots) {
                        if (i.getString("sub").toString() == currentSub) {
                            Log.e("h", i.data.values.toString())
                            db.collection("users/")
                                .document(email)
                                .collection("subjectScores")
                                .document(i.id)
                                .delete()
                                .addOnSuccessListener {
                                    Log.d("status", "DocumentSnapshot successfully deleted!")
                                    //Go back to MainScore
                                    view?.findNavController()
                                        ?.navigate(R.id.action_secondScore_to_mainScore)
                                }
                                .addOnFailureListener { e ->
                                    Log.w(
                                        "status",
                                        "Error deleting document",
                                        e
                                    )
                                }
                        }
                    }
                }
            })
    }

    //Make recycler views all 3 of them mul 1,2,3
    private fun buildRecyclerView(
        mulList: MutableList<Diem>,
        i: Int
    ) {
        val scores = mutableListOf<Double>()
        val months = mutableListOf<Int>()
        for ((key, value) in mulList) {
            scores.add(key)
            months.add(value.toInt())
        }
        //I to decide to build for mul 1 2 or 3
        //when is like if, if 1 or 2 or 3
        when (i) {
            1 -> {
                binding.recyclerView1.apply {
                    //layoutManager to decide how the items spread, in this case in spread linearly
                    layoutManager = LinearLayoutManager(activity)
                    //Every recycler view need an adapter, this adapter will need mulList
                    // TODO remove redundant scores and months
                    adapter = SecondScoreAdapter(scores, months, mulList)
                }
            }
            2 -> {
                binding.recyclerView2.apply {
                    layoutManager = LinearLayoutManager(activity)
                    adapter = SecondScoreAdapter(scores, months, mulList)
                }
            }
            else -> {
                binding.recyclerView3.apply {
                    layoutManager = LinearLayoutManager(activity)
                    adapter = SecondScoreAdapter(scores, months, mulList)
                }
            }
        }
    }
}