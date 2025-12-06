package com.example.securesocialapp.network

import com.example.securesocialapp.data.model.request.RefreshRequest
import com.example.securesocialapp.data.model.response.TokenPair
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/refresh")
    fun refreshToken(@Body request: RefreshRequest): Call<TokenPair>
}