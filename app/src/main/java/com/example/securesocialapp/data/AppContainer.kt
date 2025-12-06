package com.example.securesocialapp.data

import android.content.Context
import com.example.securesocialapp.data.datastore.UserPreferences
import com.example.securesocialapp.data.repository.AuthRepository
import com.example.securesocialapp.data.repository.AuthRepositoryImpl
import com.example.securesocialapp.data.repository.PostRepository
import com.example.securesocialapp.data.repository.PostRepositoryImpl
import com.example.securesocialapp.network.ApiService
import com.example.securesocialapp.network.AuthApi
import com.example.securesocialapp.network.AuthInterceptor
import com.example.securesocialapp.network.TokenAuthenticator
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit

interface AppContainer{
    val postRepository: PostRepository
    val authRepository: AuthRepository
}

class DefaultAppContainer(private val context: Context) : AppContainer {

    private val BASE_URL = "http://10.7.97.46:8080/"
    private val userPreferences = UserPreferences(context)

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    // We need a simple Retrofit instance JUST to create the AuthApi.
    // This allows the Authenticator to call "refresh" without getting stuck in a loop.
    private val authRetrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    private val authApi: AuthApi by lazy {
        authRetrofit.create(AuthApi::class.java)
    }


    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(userPreferences))       // Adds "Bearer" token
            .authenticator(TokenAuthenticator(userPreferences, authApi)) // Handles 401 Retries, authApi from above
            .build()
    }
    // Uses the client that has the security logic attached
    private val mainRetrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    private val apiService: ApiService by lazy {
        mainRetrofit.create(ApiService::class.java)
    }
    override val postRepository: PostRepository by lazy {
        PostRepositoryImpl(apiService)
    }
    override val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(apiService, userPreferences)
    }
}
