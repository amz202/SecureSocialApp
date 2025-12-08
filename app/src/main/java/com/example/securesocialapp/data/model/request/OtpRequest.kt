package com.example.securesocialapp.data.model.request

import kotlinx.serialization.Serializable

@Serializable

data class OtpRequest(
    val email: String,
    val otp: String
)