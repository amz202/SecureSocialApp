package com.example.securesocial.data.model.response

import kotlinx.serialization.Serializable

@Serializable
data class PostLikesResponse(
    val username: String,
    val postId: String,
    val likedAt: Long
)