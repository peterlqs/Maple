package com.example.maple.ui.experiment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.maple.data.Task

class MainExperimentViewModel : ViewModel() {

    //All user's task data
    private val _allTask = MutableLiveData<MutableList<Task>>()
    val allTask: LiveData<MutableList<Task>> = _allTask
    fun setAllTask(allTask: MutableList<Task>) {
        _allTask.value = allTask
    }

}