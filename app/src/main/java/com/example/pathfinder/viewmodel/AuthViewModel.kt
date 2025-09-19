package com.example.pathfinder.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pathfinder.graphql.LoginMutation
import com.example.pathfinder.graphql.RegisterMutation
import com.example.pathfinder.model.AuthSuccessData
import com.example.pathfinder.network.apolloClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Represents the state of the authentication UI
sealed interface AuthState {
    object Idle : AuthState
    object Loading : AuthState
    data class Success(val data: AuthSuccessData) : AuthState
    data class Error(val message: String) : AuthState
}

class AuthViewModel : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState = _authState.asStateFlow()

    fun register(email: String, password: String, name: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = apolloClient.mutation(
                    RegisterMutation(
                        email = email,
                        password = password,
                        name = name
                    )
                ).execute()

                // 1. Get the payload from the 'register' field
                val registerPayload = response.data?.register

                if (registerPayload != null && !response.hasErrors()) {
                    // 2. Access the data through the 'authPayload' property (named after your fragment)
                    val fragmentData = registerPayload.authPayload

                    val successData = AuthSuccessData(
                        token = fragmentData.token,
                        userId = fragmentData.user.id,
                        userName = fragmentData.user.name,
                        userEmail = fragmentData.user.email
                    )
                    _authState.value = AuthState.Success(successData)
                } else {
                    // Added missing error handling
                    val errorMessage = response.errors?.firstOrNull()?.message ?: "Registration failed"
                    _authState.value = AuthState.Error(errorMessage)
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "An unknown error occurred")
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = apolloClient.mutation(
                    LoginMutation(
                        email = email,
                        password = password
                    )
                ).execute()

                // 1. Get the payload from the 'login' field
                val loginPayload = response.data?.login

                if (loginPayload != null && !response.hasErrors()) {
                    // 2. Access the data through the 'authPayload' property (named after your fragment)
                    val fragmentData = loginPayload.authPayload

                    val successData = AuthSuccessData(
                        token = fragmentData.token,
                        userId = fragmentData.user.id,
                        userName = fragmentData.user.name,
                        userEmail = fragmentData.user.email
                    )
                    _authState.value = AuthState.Success(successData)
                } else {
                    val errorMessage = response.errors?.firstOrNull()?.message ?: "Login failed"
                    _authState.value = AuthState.Error(errorMessage)
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "An unknown error occurred")
            }
        }
    }

    // Function to reset the state, e.g., after an error message is shown
    fun resetState() {
        _authState.value = AuthState.Idle
    }
}