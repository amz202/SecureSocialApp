package com.example.securesocialapp.data.model.response

import kotlinx.serialization.Serializable

@Serializable

data class PostResponse(
    val id: String,
    val title: String,
    val content: String,
    val tag: String,
    val createdAt: Long,
    val authorName: String,
    val likeCount: Long,
    val viewCount: Long,
    val commentCount: Long = 0,
    val liked: Boolean
)
