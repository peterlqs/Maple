package com.example.maple.ui.score

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.maple.data.SubjectData

class ScoreViewModel : ViewModel() {
    // To save something in view model
    // 1. private val _(name) = MutableLiveData<Type of data>()
    // 2. val (name): LiveData<Type of data same as above> = _(name)
    // 3. fun set(name)((name): Type of data) {
    // 4.     _name.value = name
    // 5. }

    //All user's subject data
    private val _subjectData = MutableLiveData<MutableList<SubjectData>>()
    val subjectData: LiveData<MutableList<SubjectData>> = _subjectData
    fun setSubjectData(subjectData: MutableList<SubjectData>) {
        _subjectData.value = subjectData
    }

    //Selected subject in recycler view
    private val _workingSubject = MutableLiveData<String>()
    val workingSubject: LiveData<String> = _workingSubject
    fun setWorkingSubject(subject: String) {
        _workingSubject.value = subject
    }
}