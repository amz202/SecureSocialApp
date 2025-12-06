package com.example.securesocialapp.data.model.request


data class PostRequest(
    val title: String,
    val content: String,
    val id: String?,
    val tag: String,
)