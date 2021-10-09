package com.example.maple.ui.experiment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.maple.adapter.ExperimentAdapter
import com.example.maple.data.Task
import com.example.maple.databinding.MainExperimentFragmentBinding

class MainExperiment : Fragment() {
    // viewModel
    private val viewModel: MainExperimentViewModel by activityViewModels()

    // Binding
    private lateinit var _binding: MainExperimentFragmentBinding
    private val binding get() = _binding

    companion object {
        const val TAG = "MainExperiment"
    }


    // Inflate
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MainExperimentFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Do stuff here man
        viewModel.allTask.observe(viewLifecycleOwner, { new ->
            binding.recyclerView.apply {
                //Type grid, 2 item horizontally
                layoutManager = LinearLayoutManager(activity)
                //Set adapter, i cant explain the second parameter
                adapter = ExperimentAdapter(new)
            }
        })
        // Input handler
        binding.button.setOnClickListener {
            viewModel.apply {
                var allTask = viewModel.allTask.value
                if (allTask == null) {
                    allTask = mutableListOf<Task>()
                }
                val task = binding.editTextTextPersonName.text.toString()
                val date = binding.editTextTextPersonName2.text.toString()
                allTask?.add(Task(task, date))
                Log.d(TAG, allTask.toString())
                setAllTask(allTask)
            }
        }
    }

}