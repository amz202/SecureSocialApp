package com.example.securesocialapp.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "securesocial_prefs")

class UserPreferences(private val context: Context) {

    companion object {
        // Token Keys
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")

        // User Profile Keys
        private val USER_ID_KEY = stringPreferencesKey("user_id")
        private val USERNAME_KEY = stringPreferencesKey("username")
        private val EMAIL_KEY = stringPreferencesKey("email")
    }

    // Save Tokens (Called after Login or Refresh)
    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        context.dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN_KEY] = accessToken
            preferences[REFRESH_TOKEN_KEY] = refreshToken
        }
    }

    // Save User Info (Called after Login/Register)
    suspend fun saveUser(id: String, username: String, email: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = id
            preferences[USERNAME_KEY] = username
            preferences[EMAIL_KEY] = email
        }
    }

    // Get Access Token (Used by AuthInterceptor)
    suspend fun getAccessToken(): String? {
        return context.dataStore.data.map { preferences ->
            preferences[ACCESS_TOKEN_KEY]
        }.first()
    }

    // Get Refresh Token (Used by TokenAuthenticator)
    suspend fun getRefreshToken(): String? {
        return context.dataStore.data.map { preferences ->
            preferences[REFRESH_TOKEN_KEY]
        }.first()
    }

    // Get User Info (Used for Profile Screen)
    suspend fun getUserInfo(): UserInfo? {
        val preferences = context.dataStore.data.first()
        val id = preferences[USER_ID_KEY] ?: return null
        val username = preferences[USERNAME_KEY] ?: return null
        val email = preferences[EMAIL_KEY] ?: return null

        return UserInfo(id, username, email)
    }

    // Logout (Clear everything)
    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }

    data class UserInfo(
        val id: String,
        val username: String,
        val email: String
    )
}