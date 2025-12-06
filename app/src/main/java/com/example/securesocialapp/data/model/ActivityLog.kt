package com.example.securesocialapp.data.model

data class ActivityLog(
    val id: String,
    val userId: String,
    val action: LogType,
    val createdAt: Long ,
    val details: String?
)
