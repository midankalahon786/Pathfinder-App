package com.example.pathfinder.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pathfinder.graphql.GetUserOnboardingDataQuery
import com.example.pathfinder.model.ProjectUI
import com.example.pathfinder.network.apolloClient
import com.example.pathfinder.network.data.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// UI states for this screen
sealed interface ProjectsUiState {
    object Loading : ProjectsUiState
    data class Success(val projects: List<ProjectUI>) : ProjectsUiState
    data class Error(val message: String) : ProjectsUiState
}

interface IProjectsViewModel {
    val uiState: StateFlow<ProjectsUiState>
    fun fetchUserProjects()
}

class ProjectsViewModel(application: Application) : AndroidViewModel(application), IProjectsViewModel {
    private val tokenManager = TokenManager(application)

    private val _uiState = MutableStateFlow<ProjectsUiState>(ProjectsUiState.Loading)
    override val uiState = _uiState.asStateFlow()

    override fun fetchUserProjects() {
        val userId = tokenManager.getUserId()
        if (userId == null) {
            _uiState.value = ProjectsUiState.Error("User not logged in.")
            return
        }

        viewModelScope.launch {
            _uiState.value = ProjectsUiState.Loading
            try {
                val response = apolloClient.query(GetUserOnboardingDataQuery(id = userId)).execute()
                val user = response.data?.getUserById

                if (user != null && !response.hasErrors()) {
                    val projects = user.projects?.mapNotNull { it }?.map {
                        ProjectUI(
                            projectId = it.id,
                            name = it.name,
                            description = it.description ?: "",
                            githubLink = it.githubLink ?: "",
                            status = it.status.name
                        )
                    } ?: emptyList()
                    _uiState.value = ProjectsUiState.Success(projects)
                } else {
                    val error = response.errors?.firstOrNull()?.message ?: "Could not fetch projects."
                    _uiState.value = ProjectsUiState.Error(error)
                }
            } catch (e: Exception) {
                _uiState.value = ProjectsUiState.Error(e.message ?: "A network error occurred.")
            }
        }
    }
}