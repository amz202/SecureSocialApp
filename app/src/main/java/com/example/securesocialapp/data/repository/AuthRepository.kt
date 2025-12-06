package com.example.securesocialapp.data.repository

import com.example.securesocialapp.data.datastore.UserPreferences
import com.example.securesocialapp.data.model.request.LoginRequest
import com.example.securesocialapp.data.model.request.OtpRequest
import com.example.securesocialapp.data.model.request.RegisterRequest
import com.example.securesocialapp.data.model.response.AuthResponse
import com.example.securesocialapp.data.model.response.TokenPair
import com.example.securesocialapp.network.ApiService
import okhttp3.ResponseBody

interface AuthRepository {
    suspend fun register(request: RegisterRequest): ResponseBody
    suspend fun login(request: LoginRequest): AuthResponse
    suspend fun checkUsername(username: String): Map<String, Boolean>
    suspend fun verifyOtp(request: OtpRequest): String
    suspend fun resendOtp(email: String): String
}

class AuthRepositoryImpl(
    private val apiService: ApiService,
) : AuthRepository {
    override suspend fun register(request: RegisterRequest): ResponseBody {
        return apiService.register(request)
    }

    override suspend fun login(request: LoginRequest): AuthResponse {
        return apiService.login(request)
    }

    override suspend fun checkUsername(username: String): Map<String, Boolean> {
        return apiService.checkUsername(username)
    }

    override suspend fun verifyOtp(request: OtpRequest): String {
        return apiService.verifyOtp(request)
    }

    override suspend fun resendOtp(email: String): String {
        return apiService.resendOtp(email)
    }
}
