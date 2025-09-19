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

    private fun handleAuthSuccess(successData: AuthSuccessData) {
        Log.d("AuthViewModel", "Saving token and userId: ${successData.userId}")
        tokenManager.saveToken(successData.token)
        tokenManager.saveUserId(successData.userId)
        _authState.value = AuthState.Success(successData)
    }

   override fun register(email: String, password: String, name: String) {
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

                val registerPayload = response.data?.register

                if (registerPayload != null && !response.hasErrors()) {
                    val fragmentData = registerPayload.authPayload

                    val successData = AuthSuccessData(
                        token = fragmentData.token,
                        userId = fragmentData.user.id,
                        userName = fragmentData.user.name,
                        userEmail = fragmentData.user.email
                    )
                    handleAuthSuccess(successData)
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

   override fun login(email: String, password: String) {
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
                    handleAuthSuccess(successData)
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

    override fun logout() {
        tokenManager.clear()
    }

    // Function to reset the state, e.g., after an error message is shown
    override fun resetState() {
        _authState.value = AuthState.Idle
    }
}

