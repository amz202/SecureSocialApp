package com.example.securesocialapp.network

import com.example.securesocialapp.data.datastore.UserPreferences
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val userPreferences: UserPreferences
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Get the Access Token from DataStore
        // We use 'runBlocking' because DataStore is async (suspend),
        // but this Interceptor function must return a value synchronously.
        // Since Retrofit runs this on a background thread, blocking here is safe.
        val token = runBlocking {
            userPreferences.getAccessToken()
        }

        // 2. If no token found (User is logged out), send request as-is
        // (This happens for Login, Register, CheckUsername, etc.)
        if (token.isNullOrBlank()) {
            return chain.proceed(originalRequest)
        }

        // 3. Create a NEW request with the Authorization Header
        val newRequest = originalRequest.newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()

        return chain.proceed(newRequest)
    }
}