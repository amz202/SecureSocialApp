package com.example.securesocialapp.ui.screen.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.securesocialapp.data.model.request.RegisterRequest
import com.example.securesocialapp.ui.navigation.LoginScreenNav
import com.example.securesocialapp.ui.navigation.OtpScreenNav
import com.example.securesocialapp.ui.viewModel.AuthViewModel
import com.example.securesocialapp.ui.viewModel.BaseUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.compareTo
import kotlin.text.get

@Composable
fun SignupScreen(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel,
    navController: NavHostController,
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    // Helpers
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    var usernameCheckJob by remember { mutableStateOf<Job?>(null) }

    val usernameUiState = authViewModel.usernameUiState
    val registerUiState = authViewModel.registerUiState

    // Validation
    val passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{9,}$".toRegex()
    val isPasswordValid = password.matches(passwordRegex)
    val usernameRegex = "^[a-z0-9_]{5,}$".toRegex()
    val isUsernameValid = username.matches(usernameRegex)


    LaunchedEffect(registerUiState) {
        if (registerUiState is BaseUiState.Success && registerUiState.data != null) {
            navController.navigate(OtpScreenNav(email))
            authViewModel.resetRegisterState()
        }
    }

    // Debounced username check
    LaunchedEffect(username) {
        usernameCheckJob?.cancel()
        if (username.isNotBlank() && isUsernameValid) {
            usernameCheckJob = scope.launch {
                delay(500)
                authViewModel.checkUsername(username)
            }
        }
    }

    val isUsernameAvailable = when (usernameUiState) {
        is BaseUiState.Success -> usernameUiState.data?.get("available") == true
        else -> null
    }

    val isUsernameChecking = usernameUiState is BaseUiState.Loading

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "SecureSocial",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Create an account",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.secondary,
            fontWeight = FontWeight.Normal
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Username field
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
            trailingIcon = {
                if (username.isNotBlank()) {
                    when {
                        !isUsernameValid -> Icon(Icons.Default.Warning, "Invalid format", tint = MaterialTheme.colorScheme.error)
                        isUsernameChecking -> CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                        isUsernameAvailable == true -> Icon(Icons.Default.CheckCircle, "Available", tint = MaterialTheme.colorScheme.primary)
                        isUsernameAvailable == false -> Icon(Icons.Default.Warning, "Taken", tint = MaterialTheme.colorScheme.error)
                    }
                }
            },
            supportingText = {
                if (username.isNotBlank()) {
                    when {
                        !isUsernameValid -> Text("5+ chars, lowercase letters, digits, or underscores only", color = MaterialTheme.colorScheme.error)
                        isUsernameAvailable == false -> Text("Username is already taken", color = MaterialTheme.colorScheme.error)
                        isUsernameAvailable == true -> Text("Username is available", color = MaterialTheme.colorScheme.primary)
                    }
                }
            },
            isError = username.isNotBlank() && (!isUsernameValid || isUsernameAvailable == false)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Email field
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password field
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),

            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "Toggle Password"
                    )
                }
            },
            isError = password.isNotBlank() && !isPasswordValid,
            supportingText = {
                if (password.isNotBlank() && !isPasswordValid) {
                    Text(
                        "Must be 9+ chars, 1 Upper, 1 Lower, 1 Digit",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Confirm Password field
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),

            trailingIcon = {
                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                    Icon(
                        imageVector = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "Toggle Password"
                    )
                }
            },
            isError = confirmPassword.isNotBlank() && password != confirmPassword,
            supportingText = {
                if (confirmPassword.isNotBlank() && password != confirmPassword) {
                    Text("Passwords do not match", color = MaterialTheme.colorScheme.error)
                }
            }
        )

        if (registerUiState is BaseUiState.Error) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Registration failed. Please try again.",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                authViewModel.register(RegisterRequest(username, email, password))
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            enabled = username.isNotBlank() &&
                    isUsernameValid &&
                    email.isNotBlank() &&
                    password.isNotBlank() &&
                    isPasswordValid &&
                    password == confirmPassword &&
                    isUsernameAvailable == true &&
                    !isUsernameChecking &&
                    registerUiState !is BaseUiState.Loading
        ) {
            if (registerUiState is BaseUiState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "Sign Up",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Already have an account?",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            TextButton(onClick = {
                navController.navigate(LoginScreenNav)
            }) {
                Text(
                    text = "Login",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}