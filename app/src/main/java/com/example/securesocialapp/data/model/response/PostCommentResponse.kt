package com.example.securesocialapp.data.model.response

import kotlinx.serialization.Serializable

@Serializable
data class PostCommentResponse(
    val comment: String,
    val createdAt: Long,
    val id: String,
    val username: String,
    val postId: String
)
