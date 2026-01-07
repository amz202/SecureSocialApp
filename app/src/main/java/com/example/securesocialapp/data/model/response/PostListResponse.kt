package com.example.securesocialapp.data.model.response

import kotlinx.serialization.Serializable

@Serializable
data class PostListResponse(
    val id: String,
    val title: String,
    val content: String,
    val tag: String,
    val createdAt: Long,
    val likeCount: Long,
    val viewCount: Long,
    val commentCount: Long,
)
