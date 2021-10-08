package com.example.maple.ui.recommendation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.maple.adapter.MainScoreAdapter
import com.example.maple.adapter.RecommendationAdapter
import com.example.maple.databinding.MainRecommendationFragmentBinding
import com.example.maple.ui.score.MainDialog

class MainRecommendation : Fragment() {

    private lateinit var viewModel: MainRecommendationViewModel

    //Binding
    private lateinit var _binding: MainRecommendationFragmentBinding
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MainRecommendationFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
            layoutManager = GridLayoutManager(activity, 2)
            //Set adapter, i cant explain the second parameter
            //adapter = RecommendationAdapter(this)
        }

    }

    //Creating cardview for recommendation
    class Song {
        private var sName: String? = null

        constructor() {}
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