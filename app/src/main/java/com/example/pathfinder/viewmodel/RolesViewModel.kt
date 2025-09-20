package com.example.pathfinder.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pathfinder.graphql.GetUserOnboardingDataQuery
import com.example.pathfinder.model.RoleUI
import com.example.pathfinder.network.apolloClient
import com.example.pathfinder.network.data.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


interface IRolesViewModel {
    val uiState: StateFlow<RolesUiState>
    fun fetchUserRoles()
}

sealed interface RolesUiState {
    object Loading : RolesUiState
    data class Success(val roles: List<RoleUI>) : RolesUiState
    data class Error(val message: String) : RolesUiState
}

class RolesViewModel(application: Application) : AndroidViewModel(application), IRolesViewModel {
    private val tokenManager = TokenManager(application)

    private val _uiState = MutableStateFlow<RolesUiState>(RolesUiState.Loading)
    override val uiState = _uiState.asStateFlow()

    override fun fetchUserRoles() {
        val userId = tokenManager.getUserId()
        if (userId == null) {
            _uiState.value = RolesUiState.Error("User not logged in.")
            return
        }

        viewModelScope.launch {
            _uiState.value = RolesUiState.Loading
            try {
                // We reuse the comprehensive query
                val response = apolloClient.query(GetUserOnboardingDataQuery(id = userId)).execute()
                val user = response.data?.getUserById

                if (user != null && !response.hasErrors()) {
                    val combinedRoles = mutableListOf<RoleUI>()

                    // Add the user's current role if it exists
                    user.currentRole?.let {
                        if (it.isNotBlank()) {
                            combinedRoles.add(RoleUI(title = it, isDesired = false))
                        }
                    }

                    // Add the user's desired career goals
                    user.careerGoals?.forEach { goal ->
                        goal?.let {
                            combinedRoles.add(RoleUI(title = "${it.title} (Desired)", isDesired = true))
                        }
                    }

                    _uiState.value = RolesUiState.Success(combinedRoles)
                } else {
                    val error = response.errors?.firstOrNull()?.message ?: "Could not fetch roles."
                    _uiState.value = RolesUiState.Error(error)
                }
            } catch (e: Exception) {
                _uiState.value = RolesUiState.Error(e.message ?: "A network error occurred.")
            }
        }
    }
}