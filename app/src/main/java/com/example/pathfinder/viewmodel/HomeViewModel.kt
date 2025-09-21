package com.example.pathfinder.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pathfinder.graphql.GetSkillsQuery
import com.example.pathfinder.model.SkillUI
import com.example.pathfinder.network.apolloClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

interface IHomeViewModel {
    /**
     * The state of the UI, representing loading, success, or error states for the home screen.
     */
    val uiState: StateFlow<HomeUiState>

    /**
     * Fetches the list of trending skills from the backend.
     */
    fun fetchTrendingSkills()
}

sealed interface HomeUiState {
    object Loading : HomeUiState
    data class Success(val trendingSkills: List<SkillUI>) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

class HomeViewModel(application: Application) : AndroidViewModel(application), IHomeViewModel {
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    override val uiState = _uiState.asStateFlow()

    override fun fetchTrendingSkills() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            try {
                val response = apolloClient.query(GetSkillsQuery()).execute()
                val skills = response.data?.getSkills

                if (skills != null && !response.hasErrors()) {
                    val trending = skills.mapNotNull { it }.map {
                        SkillUI(
                            id = it.id,
                            name = it.name,
                            category = it.category ?: "General"
                        )
                    }
                    _uiState.value = HomeUiState.Success(trending)
                } else {
                    val error = response.errors?.firstOrNull()?.message ?: "Could not fetch skills."
                    _uiState.value = HomeUiState.Error(error)
                }
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: "A network error occurred.")
            }
        }
    }
}