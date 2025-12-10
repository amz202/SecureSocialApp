package com.example.securesocialapp.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
object LoginScreenNav

@Serializable
object SignupScreenNav

@Serializable
data class OtpScreenNav(
    val email: String
)

@Serializable
object PostsScreenNav

@Serializable
object ActivityLogScreenNav