package com.example.maple.data

data class SubjectData(
    val month: Int? = null,
    val mul: Int? = null,
    val score: Double? = null,
    val sub: String? = null,
    val id: Int? = null,
)

data class AverageSubject(
    val sub: String? = null,
    val score: Double? = null
)