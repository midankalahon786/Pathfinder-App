package com.example.pathfinder.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pathfinder.graphql.LoginMutation
import com.example.pathfinder.graphql.RegisterMutation
import com.example.pathfinder.model.AuthSuccessData
import com.example.pathfinder.network.apolloClient
import com.example.pathfinder.network.data.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

interface IAuthViewModel {
    val authState: StateFlow<AuthState>
    fun register(email: String, password: String, name: String)
    fun login(email: String, password: String)
    fun logout()
    fun resetState()
}

sealed interface AuthState {
    object Idle : AuthState
    object Loading : AuthState
    data class Success(val data: AuthSuccessData) : AuthState
    data class Error(val message: String) : AuthState
}

class AuthViewModel(application: Application) : AndroidViewModel(application), IAuthViewModel {

    private val tokenManager = TokenManager(application)

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    override val authState = _authState.asStateFlow()

    init {
        // Automatically check for a saved session when the ViewModel is created
        Log.d("AuthViewModel", "ViewModel initialized. Checking for existing session.")
        checkSession()
    }

    private fun checkSession() {
        viewModelScope.launch {
            val savedUserId = tokenManager.getUserId()
            val savedToken = tokenManager.getToken()

            if (savedUserId != null && savedToken != null) {
                // If a token and userId exist, assume the user is logged in
                Log.d("AuthViewModel", "Existing session found. Restoring session.")
                _authState.value = AuthState.Success(
                    AuthSuccessData(
                        token = savedToken,
                        userId = savedUserId,
                        userName = "", // User name is not saved, so we can use a placeholder
                        userEmail = "" // Email is not saved, so we can use a placeholder
                    )
                )
            } else {
                Log.d("AuthViewModel", "No existing session found.")
                _authState.value = AuthState.Idle
            }
        }
    }

    private fun handleAuthSuccess(successData: AuthSuccessData) {
        Log.d("AuthViewModel", "Saving token and userId: ${successData.userId}")
        tokenManager.saveToken(successData.token)
        tokenManager.saveUserId(successData.userId)
        _authState.value = AuthState.Success(successData)
    }

    override fun register(email: String, password: String, name: String) {
        Log.d("AuthViewModel", "Attempting to register user with email: $email")
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                Log.d("AuthViewModel", "Executing register mutation...")
                val response = apolloClient.mutation(
                    RegisterMutation(
                        email = email,
                        password = password,
                        name = name
                    )
                ).execute()

                val registerPayload = response.data?.register

                if (registerPayload != null && !response.hasErrors()) {
                    Log.d("AuthViewModel", "Registration successful. Processing response...")
                    val fragmentData = registerPayload.authPayload

                    val successData = AuthSuccessData(
                        token = fragmentData.token,
                        userId = fragmentData.user.id,
                        userName = fragmentData.user.name,
                        userEmail = fragmentData.user.email
                    )
                    handleAuthSuccess(successData)
                } else {
                    val errorMessage = response.errors?.firstOrNull()?.message ?: "Registration failed"
                    Log.e("AuthViewModel", "Registration failed. Error: $errorMessage")
                    _authState.value = AuthState.Error(errorMessage)
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Exception during registration: ${e.message}", e)
                _authState.value = AuthState.Error(e.message ?: "An unknown error occurred")
            }
        }
    }

    override fun login(email: String, password: String) {
        Log.d("AuthViewModel", "Attempting to log in user with email: $email")
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                Log.d("AuthViewModel", "Executing login mutation...")
                val response = apolloClient.mutation(
                    LoginMutation(
                        email = email,
                        password = password
                    )
                ).execute()

                val loginPayload = response.data?.login

                if (loginPayload != null && !response.hasErrors()) {
                    Log.d("AuthViewModel", "Login successful. Processing response...")
                    val fragmentData = loginPayload.authPayload

                    val successData = AuthSuccessData(
                        token = fragmentData.token,
                        userId = fragmentData.user.id,
                        userName = fragmentData.user.name,
                        userEmail = fragmentData.user.email
                    )
                    handleAuthSuccess(successData)
                } else {
                    val errorMessage = response.errors?.firstOrNull()?.message ?: "Login failed"
                    Log.e("AuthViewModel", "Login failed. Error: $errorMessage")
                    _authState.value = AuthState.Error(errorMessage)
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Exception during login: ${e.message}", e)
                _authState.value = AuthState.Error(e.message ?: "An unknown error occurred")
            }
        }
    }

    override fun logout() {
        Log.d("AuthViewModel", "Clearing user session data.")
        tokenManager.clear()
        _authState.value = AuthState.Idle // Set state to idle after logout
    }

    override fun resetState() {
        Log.d("AuthViewModel", "Resetting authentication state to Idle.")
        _authState.value = AuthState.Idle
    }
}