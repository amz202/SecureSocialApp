package com.example.securesocialapp.data.model.request

import kotlinx.serialization.Serializable

@Serializable

data class RefreshRequest(
    val refreshToken: String
)