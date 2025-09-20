package com.example.pathfinder.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pathfinder.graphql.GetUserByIdQuery
import com.example.pathfinder.model.UserSkillUI
import com.example.pathfinder.network.apolloClient
import com.example.pathfinder.network.data.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


interface ISkillsViewModel {
    val uiState: StateFlow<SkillsUiState>
    fun fetchUserSkills()
}
// UI states for this screen
sealed interface SkillsUiState {
    object Loading : SkillsUiState
    data class Success(val userSkills: List<UserSkillUI>) : SkillsUiState
    data class Error(val message: String) : SkillsUiState
}

class SkillsViewModel(application: Application) : AndroidViewModel(application), ISkillsViewModel {
    private val tokenManager = TokenManager(application)

    private val _uiState = MutableStateFlow<SkillsUiState>(SkillsUiState.Loading)
    override val uiState = _uiState.asStateFlow()

    override fun fetchUserSkills() {
        val userId = tokenManager.getUserId()
        if (userId == null) {
            _uiState.value = SkillsUiState.Error("User not logged in.")
            return
        }

        viewModelScope.launch {
            _uiState.value = SkillsUiState.Loading
            try {
                // We can reuse the GetUserByIdQuery
                val response = apolloClient.query(GetUserByIdQuery(id = userId)).execute()
                val user = response.data?.getUserById

                if (user != null && !response.hasErrors()) {
                    val skills = user.skills?.mapNotNull { it }?.map {
                        UserSkillUI(
                            userSkillId = it.id,
                            skillId = it.skill?.id ?: "",
                            name = it.skill?.name ?: "Unknown Skill",
                            level = it.level ?: "N/A"
                        )
                    } ?: emptyList()
                    _uiState.value = SkillsUiState.Success(skills)
                } else {
                    val error = response.errors?.firstOrNull()?.message ?: "Could not fetch skills."
                    _uiState.value = SkillsUiState.Error(error)
                }
            } catch (e: Exception) {
                _uiState.value = SkillsUiState.Error(e.message ?: "A network error occurred.")
            }
        }
    }
}