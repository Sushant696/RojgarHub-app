package com.example.rojgarhub.model

data class applicationModel(
    val applicationId: String = "",
    val jobId: String = "",
    val candidateId: String = "",
    val status: String = "PENDING",
    val appliedDate: Long = System.currentTimeMillis()
)