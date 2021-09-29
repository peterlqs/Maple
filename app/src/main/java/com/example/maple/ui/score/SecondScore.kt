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
        loadListener()
        //Delete subject btn
        val currentSubject = viewModel.workingSubject.value.toString()
        val btnDelete = binding.btnDelete
        btnDelete.setOnClickListener {
            deleteSubject(email, currentSubject)
        }
        //Set subject name
        binding.subjectName.text = viewModel.workingSubject.value
        //First set dialog on click
        binding.fabAdd.setOnClickListener {
            //fireStoreListener2!!.remove()
            val dialog = SecondDialog()
            dialog.show(childFragmentManager, "customDialog")
        }
        //Set the 3 toggle recycler view, can someone fix this DRY code
        binding.down1.setOnClickListener {
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
        //Delete sub
//        binding.deleteSub.setOnClickListener{
//            deleteSubject(viewModel.email.value.toString(),viewModel.workingSubject.value.toString(),view)
//            view.findNavController().navigate(R.id.action_monhocDiem_to_monhocResult)
//        }


    }

    private fun loadListener() {
        //add listener, load score
        val currentSubject = viewModel.workingSubject.value.toString()
        Log.d(TAG, currentSubject.toString())

        val registration = db.collection("users")
            .document(email)
            .collection("subjectScores")
            .addSnapshotListener { value, e ->
                //Error handling
                if (e != null) {
                    Log.w(MainScore.TAG, "listen:error", e)
                    return@addSnapshotListener
                }
                val mul1List = mutableListOf<Diem>()
                val mul2List = mutableListOf<Diem>()
                val mul3List = mutableListOf<Diem>()
                val listId = mutableListOf<String>()
                //Average score
                var totalMul = 0
                var totalScore = 0.0
                var id = ""
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
                var count = 0
                for (doc in listData) {
                    if (doc.sub != currentSubject || doc.month
                            ?.toInt() == 0
                    ) continue
                    //Add average score
                    //If added to score then pass
                    val currentMul = doc.mul
                    val currentScore = doc.score
                    val currentMonth = doc.month?.toDouble()
                    val currentID = listId[count].trim()
                    if (currentScore != null && currentMonth != null && currentMul != null) {
                        totalMul += currentMul
                        totalScore += currentScore * currentMul
                        when (currentMul) {
                            1 -> {
                                //                                mul1List.put(currentScore, currentMonth)
                                mul1List.add(Diem(currentScore, currentMonth, currentID))
                            }
                            2 -> {
                                //                                mul2List.put(currentScore, currentMonth)
                                mul2List.add(Diem(currentScore, currentMonth, currentID))
                            }
                            else -> {
                                //                                mul3List.put(currentScore, currentMonth)
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

//                Log.d("dsad",mul1List.toString())
//                Log.d("dsad",mul2List.toString())
//                Log.d("dsad",mul3List.toString())
                //Create 3 different recyclerView
                buildRecyclerView(mul1List, 1)
                buildRecyclerView(mul2List, 2)
                buildRecyclerView(mul3List, 3)

            }
    }

    private fun deleteSubject(email: String, currentSub: String) {
        var subjectData = viewModel.subjectData.value
        subjectData = subjectData!!.filter { it.sub != currentSub }.toMutableList()
        viewModel.setSubjectData(subjectData)
        Log.d("sadf", viewModel.subjectData.value.toString())

        db.collection("users/")
            .document(email)
            .collection("subjectScores")
            .addSnapshotListener(EventListener { documentSnapshots, e ->
                if (e != null) {
                    Log.e("TAG", "Listen failed", e)
                    return@EventListener
                }
                if (documentSnapshots != null) {
                    for (i in documentSnapshots) {
                        Log.e("h", i.getString("sub").toString())
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

    private fun buildRecyclerView(
//        mulList: MutableMap<Double, Double>,
        mulList: MutableList<Diem>,
        i: Int
    ) {
        val scores = mutableListOf<Double>()
        val months = mutableListOf<Int>()
        for ((key, value) in mulList) {
            scores.add(key)
            months.add(value.toInt())
        }
        when (i) {
            1 -> {
                binding.recyclerView1.apply {
                    layoutManager = LinearLayoutManager(activity)
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