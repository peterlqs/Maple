package com.example.maple.ui.recommendation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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

        // Do stuff here

        val avatar = binding.userAva
        val fabChangeAva = binding.fabAddAva

        fabChangeAva.setOnClickListener {
            val dialog = MainDialog()
            dialog.show(childFragmentManager, "customDialog")

            //This part is for choosing avatar from our storage
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