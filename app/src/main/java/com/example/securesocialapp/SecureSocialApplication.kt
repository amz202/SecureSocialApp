package com.example.securesocialapp

import android.app.Application
import com.example.securesocialapp.data.AppContainer
import com.example.securesocialapp.data.DefaultAppContainer
import com.example.securesocialapp.data.datastore.UserPreferences

class SecureSocialApplication: Application() {
    lateinit var container: AppContainer
    lateinit var userPreferences: UserPreferences

    override fun onCreate() {
        super.onCreate()
        userPreferences = UserPreferences(this)
        container = DefaultAppContainer(this)
    }
}