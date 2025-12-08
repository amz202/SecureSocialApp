package com.example.securesocialapp.ui.screen.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.securesocialapp.data.model.request.OtpRequest
import com.example.securesocialapp.ui.viewModel.AuthViewModel
import com.example.securesocialapp.ui.viewModel.BaseUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun OtpScreen(
    modifier: Modifier = Modifier,
    email: String,
    authViewModel: AuthViewModel,
    onVerificationSuccess: () -> Unit = {}
) {
    var otp by remember { mutableStateOf("") }
    var timeLeft by remember { mutableStateOf(300) } // 5 minutes in seconds
    var isResendEnabled by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val otpUiState = authViewModel.otpUiState

    // Countdown timer
    LaunchedEffect(timeLeft) {
        if (timeLeft > 0) {
            delay(1000)
            timeLeft--
        } else {
            isResendEnabled = true
        }
    }

    // Handle OTP verification success
    LaunchedEffect(otpUiState) {
        if (otpUiState is BaseUiState.Success && otpUiState.data != null) {
            onVerificationSuccess()
        }
    }

    val minutes = timeLeft / 60
    val seconds = timeLeft % 60

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Verify Your Email",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "We've sent a verification code to",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )

        Text(
            text = email,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = otp,
            onValueChange = { if (it.length <= 6 && it.all { char -> char.isDigit() }) otp = it },
            label = { Text("Enter OTP") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.NumberPassword
            ),
            isError = otpUiState is BaseUiState.Error,
            supportingText = {
                if (otpUiState is BaseUiState.Error) {
                    Text(
                        "Invalid or expired OTP. Please try again.",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Timer display
        if (!isResendEnabled) {
            Text(
                text = "Code expires in: ${String.format("%02d:%02d", minutes, seconds)}",
                style = MaterialTheme.typography.bodyMedium,
                color = if (timeLeft < 60) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
            )
        } else {
            Text(
                text = "Code expired",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                authViewModel.verifyOtp(OtpRequest(email, otp))
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = otp.length == 6 && otpUiState !is BaseUiState.Loading
        ) {
            if (otpUiState is BaseUiState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Verify OTP")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = {
                scope.launch {
                    authViewModel.resendOtp(email)
                    timeLeft = 300
                    isResendEnabled = false
                    otp = ""
                }
            },
            enabled = isResendEnabled
        ) {
            Text(
                text = if (isResendEnabled) "Resend OTP" else "Resend OTP (available after expiry)",
                color = if (isResendEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
            )
        }
    }
}
