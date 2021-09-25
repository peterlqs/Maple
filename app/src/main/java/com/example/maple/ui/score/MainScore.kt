package com.example.maple.ui.score

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.maple.databinding.MainScoreFragmentBinding

class MainScore : Fragment() {
    //View Model
    private val viewModel: MainScoreViewModel by activityViewModels()

    //Binding
    private lateinit var _binding: MainScoreFragmentBinding
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = MainScoreFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Do stuff here

        //Fab button
        val fab = binding.addMain
        fab.setOnClickListener {
            var dialog = MainDialog()
            dialog.show(childFragmentManager, "customDialog")
        }


    }
}