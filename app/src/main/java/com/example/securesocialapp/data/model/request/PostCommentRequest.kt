package com.example.securesocialapp.data.model.request

import kotlinx.serialization.Serializable

@Serializable
data class PostCommentRequest(
    val comment: String,
)