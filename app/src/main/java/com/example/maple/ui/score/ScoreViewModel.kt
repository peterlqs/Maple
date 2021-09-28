package com.example.maple.ui.score

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.maple.data.SubjectData

class ScoreViewModel : ViewModel() {

    //All user's subject data
    private val _subjectData = MutableLiveData<MutableList<SubjectData>>()
    val subjectData: LiveData<MutableList<SubjectData>> = _subjectData
    fun setSubjectData(subjectData: MutableList<SubjectData>) {
        _subjectData.value = subjectData
    }


}