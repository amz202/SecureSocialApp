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
    private val userPreferences: UserPreferences
) : AuthRepository {
    override suspend fun register(request: RegisterRequest): ResponseBody {
        return apiService.register(request)
    }

    override suspend fun login(request: LoginRequest): AuthResponse {
        val response = apiService.login(request)

        userPreferences.saveTokens(response.accessToken, response.refreshToken)

        userPreferences.saveUser(
            id = response.userId,
            username = response.username,
            email = response.email
        )

        return response
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
