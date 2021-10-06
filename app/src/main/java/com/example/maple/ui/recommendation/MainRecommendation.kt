package com.example.maple.ui.recommendation

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.maple.R
import com.example.maple.databinding.MainRecommendationFragmentBinding
import com.example.maple.databinding.MainScoreFragmentBinding
import com.example.maple.ui.score.MainDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainRecommendation : Fragment() {

    private lateinit var viewModel: MainRecommendationViewModel

    //Binding
    private lateinit var _binding: MainRecommendationFragmentBinding
    private val binding get() = _binding

    val avatar = binding.userAva
    val fabChangeAva = binding.fabAddAva


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.main_recommendation_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainRecommendationViewModel::class.java)
        // TODO: Use the ViewModel

        fabChangeAva.setOnClickListener {
            val dialog = MainDialog()
            dialog.show(childFragmentManager, "customDialog")

            //This part is for choosing avatar from our storage
        }
    }

}