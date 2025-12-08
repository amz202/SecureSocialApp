package com.example.securesocialapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import com.example.securesocialapp.data.datastore.UserPreferences
import com.example.securesocialapp.ui.navigation.AppNavigation
import com.example.securesocialapp.ui.theme.SecureSocialAppTheme
import com.example.securesocialapp.ui.viewModel.AuthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userPreferences = UserPreferences(this)
        val authViewModel: AuthViewModel = ViewModelProvider(this, AuthViewModel.authFactory)[AuthViewModel::class.java]
        enableEdgeToEdge()
        setContent {
            SecureSocialAppTheme {
                AppNavigation(authViewModel)
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SecureSocialAppTheme {
        Greeting("Android")
    }
}