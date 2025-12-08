package com.example.securesocialapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.securesocialapp.ui.screen.auth.SignupScreen
import com.example.securesocialapp.ui.viewModel.AuthViewModel
import androidx.navigation.compose.composable
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.securesocialapp.ui.screen.HomeScreen
import com.example.securesocialapp.ui.screen.auth.LoginScreen
import com.example.securesocialapp.ui.screen.auth.OtpScreen

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = LoginScreenNav
    ){
        composable<SignupScreenNav>{
            SignupScreen(
                authViewModel = authViewModel,
                navController = navController
            )
        }

        composable<LoginScreenNav>{
            LoginScreen(
                authViewModel = authViewModel,
                navController = navController
            )
        }

        composable<OtpScreenNav> {
            val args = it.toRoute<OtpScreenNav>()
            OtpScreen(
                email = args.email,
                authViewModel = authViewModel,
                navController = navController
            )
        }

        composable<HomeScreenNav> {
            HomeScreen()
        }
    }
}