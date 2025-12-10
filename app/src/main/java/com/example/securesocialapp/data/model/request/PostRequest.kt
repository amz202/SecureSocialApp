package com.example.securesocialapp.data.model.request

import kotlinx.serialization.Serializable

@Serializable

data class PostRequest(
    val title: String,
    val content: String,
    val tag: String,
)