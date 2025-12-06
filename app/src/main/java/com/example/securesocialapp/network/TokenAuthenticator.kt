package com.example.securesocialapp.network

import com.example.securesocialapp.data.datastore.UserPreferences
import com.example.securesocialapp.data.model.request.RefreshRequest
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator(
    private val userPreferences: UserPreferences,
    private val authApi: AuthApi
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        // Safety Check: If we've already tried to refresh 3 times for this specific request, give up.
        if (responseCount(response) >= 3) {
            return null
        }

        // Get the stored Refresh Token (Blocking wait)
        val refreshToken = runBlocking {
            userPreferences.getRefreshToken()
        }

        if (refreshToken == null) {
            return null
        }

        // Make the Network Call to Refresh
        // We use .execute() instead of .enqueue() because we are already on a background thread
        // and we need the result *right now* before we can retry the original request.
        try {
            val refreshResponse = authApi.refreshToken(RefreshRequest(refreshToken)).execute()

            if (refreshResponse.isSuccessful) {
                val newTokens = refreshResponse.body()

                if (newTokens != null) {
                    // Save the new tokens
                    runBlocking {
                        userPreferences.saveTokens(newTokens.accessToken, newTokens.refreshToken)
                    }
                    // Rewrite the original request
                    return response.request.newBuilder()
                        .header("Authorization", "Bearer ${newTokens.accessToken}")
                        .build()
                }
            }
        } catch (e: Exception) {
            return null
        }

        // If Refresh Failed
        // watch for 401s in your ViewModel to trigger login.
        return null
    }

    private fun responseCount(response: Response): Int {
        var result = 1
        var prior = response.priorResponse
        while (prior != null) {
            result++
            prior = prior.priorResponse
        }
        return result
    }
}