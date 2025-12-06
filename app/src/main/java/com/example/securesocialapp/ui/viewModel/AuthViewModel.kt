package com.example.securesocialapp.ui.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.securesocialapp.SecureSocialApplication
import com.example.securesocialapp.data.datastore.UserPreferences
import com.example.securesocialapp.data.model.request.LoginRequest
import com.example.securesocialapp.data.model.request.OtpRequest
import com.example.securesocialapp.data.model.response.AuthResponse
import com.example.securesocialapp.data.repository.AuthRepository
import kotlinx.coroutines.launch

typealias LoginUiState = BaseUiState<AuthResponse?>
typealias OtpUiState = BaseUiState<String?>
typealias UsernameUiState = BaseUiState<Map<String, Boolean>?>

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val userPreferences: UserPreferences
): ViewModel() {

    var loginUiState: LoginUiState by mutableStateOf(BaseUiState.Success(null))
        private set

    var otpUiState: OtpUiState by mutableStateOf(BaseUiState.Success(null))
        private set

    var usernameUiState: UsernameUiState by mutableStateOf(BaseUiState.Success(null))
        private set

    fun login(request: LoginRequest) {
        viewModelScope.launch {
            loginUiState = BaseUiState.Loading
            try {
                val response = authRepository.login(request)
                userPreferences.saveTokens(response.accessToken, response.refreshToken)

                userPreferences.saveUser(
                    id = response.userId,
                    username = response.username,
                    email = response.email
                )
                loginUiState = BaseUiState.Success(response)
            } catch (e: Exception) {
                loginUiState = BaseUiState.Error
            }
        }
    }

    fun checkUsername(username: String) {
        viewModelScope.launch {
            usernameUiState = BaseUiState.Loading
            try {
                val response = authRepository.checkUsername(username)
                usernameUiState = BaseUiState.Success(response)
            } catch (e: Exception) {
                usernameUiState = BaseUiState.Error
            }
        }
    }

    fun resendOtp(email: String) {
        viewModelScope.launch {
            otpUiState = BaseUiState.Loading
            try {
                val response = authRepository.resendOtp(email)
                otpUiState = BaseUiState.Success(response)
            } catch (e: Exception) {
                otpUiState = BaseUiState.Error
            }
        }
    }

    fun verifyOtp(request: OtpRequest){
        viewModelScope.launch {
            otpUiState = BaseUiState.Loading
            try {
                val response = authRepository.verifyOtp(request)
                otpUiState = BaseUiState.Success(response)
            } catch (e: Exception) {
                otpUiState = BaseUiState.Error
            }
        }
    }

    fun resetLoginState() {
        loginUiState = BaseUiState.Success(null)
    }

    companion object {
        val authFactory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app =
                    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as SecureSocialApplication
                AuthViewModel(app.container.authRepository, app.userPreferences)
            }
        }
    }
}