package com.example.maple.ui.recommendation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.maple.adapter.SubjectAdapter
import com.example.maple.data.SubjectInfo
import com.example.maple.databinding.FragmentSubjectRecommendBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*


class SubjectRecommend : Fragment() {

    private lateinit var _binding: FragmentSubjectRecommendBinding
    private val binding get() = _binding
    private lateinit var database: DatabaseReference
    val calendar: Calendar = Calendar.getInstance(TimeZone.getDefault())

    val args: SubjectRecommendArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSubjectRecommendBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val subjectName = binding.subjectName
        subjectName.text = args.subjectName
        database = Firebase.database.reference
        val month = calendar.get(Calendar.MONTH) + 1
        Log.e("Month", month.toString())
        database.child("subjects").child("12").child(args.subjectName.capitalize())
            .child(month.toString()).get()
            .addOnSuccessListener { value ->
                if (value.value != null) {
//                    val data = it.getValue<SubjectInfo>()
//                    Log.d("Recommend", data?.link.toString())
                    Log.d("ad", value.value.toString())
                    var subjectList =
                        value.value.toString().substring(1, value.value.toString().length - 1)
                            .split(",").associate {
                                val (left, right) = it.trim().split("=", limit = 2)
                                left to right.toString()
                            }
                    subjectList =
                        subjectList.toList().sortedBy { (_, value) -> value.length }.toMap()

                    val allThings = mutableListOf<SubjectInfo>()
                    for ((key, value) in subjectList) {
                        allThings.add(SubjectInfo(key, value))
                    }


                    val subjectView = binding.recyclerView
                    subjectView.apply {
                        //Type grid, 2 item horizontally
                        layoutManager = LinearLayoutManager(activity)
                        //Set adapter, i cant explain the second parameter
                        adapter = SubjectAdapter(allThings)

                        subjectView.setHasFixedSize(true)
//                }
                    }
                }

            }.addOnFailureListener {
                Log.e("Subjects", "Retrieval Failed")
            }


    }
}