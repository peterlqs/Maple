package com.example.maple.data

import com.google.firebase.database.IgnoreExtraProperties

// Data for each score for MainScore
data class SubjectData(
    val month: Int? = null,
    val mul: Int? = null,
    val score: Double? = null,
    val sub: String? = null,
    val id: Int? = null,
)

// Average score for MainScore
data class AverageSubject(
    val sub: String? = null,
    val score: Double? = null
)

// Each Diem object for SecondScore
data class Diem(
    val diem: Double, val thang: Double, val id: String
)

data class Task(
    val task: String, val date: String
)

data class Score(
    val subject: String,
    val score1: List<String>,
    val score2: String,
    val score3: String
)

data class Notification(
    val time: String,
    val content: String
)

@IgnoreExtraProperties
data class SubjectInfo(
    val section: String,
    val link: String
) {

}

// Interface to pass which subject was clicked on from MainScore to SecondScore
interface WhichSubject {
    fun subjectName(subject: String)
}