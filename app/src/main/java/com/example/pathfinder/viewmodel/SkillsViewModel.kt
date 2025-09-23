package com.example.pathfinder.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pathfinder.graphql.AddUserSkillMutation
import com.example.pathfinder.graphql.GetSkillsQuery
import com.example.pathfinder.graphql.GetUserByIdQuery
import com.example.pathfinder.graphql.RemoveUserSkillMutation
import com.example.pathfinder.model.UserSkillUI
import com.example.pathfinder.network.apolloClient
import com.example.pathfinder.network.data.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// 1. UPDATED INTERFACE
interface ISkillsViewModel {
    val uiState: StateFlow<SkillsUiState>
    val allSkills: StateFlow<List<GetSkillsQuery.GetSkill>> // For the dialog search
    fun fetchUserSkills()
    fun fetchAllSkills() // To populate the search
    fun addUserSkill(skillId: String, level: String) // To add a new skill
    fun removeUserSkill(userSkillId: String)
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

    // 2. ADDED STATE FOR ALL SKILLS
    private val _allSkills = MutableStateFlow<List<GetSkillsQuery.GetSkill>>(emptyList())
    override val allSkills = _allSkills.asStateFlow()


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

    // 3. ADDED FUNCTION TO FETCH ALL SKILLS
    override fun fetchAllSkills() {
        viewModelScope.launch {
            try {
                val response = apolloClient.query(GetSkillsQuery()).execute()
                _allSkills.value = response.data?.getSkills?.mapNotNull { it } ?: emptyList()
            } catch (e: Exception) {
                Log.e("SkillsViewModel", "Failed to fetch all skills: ${e.message}")
            }
        }
    }

    // 4. ADDED FUNCTION TO ADD A USER SKILL
    override fun addUserSkill(skillId: String, level: String) {
        val userId = tokenManager.getUserId()
        if (userId == null) {
            // Optionally, you could set an error state here
            Log.e("SkillsViewModel", "Cannot add skill, user not logged in")
            return
        }

        viewModelScope.launch {
            try {
                val response = apolloClient.mutation(
                    AddUserSkillMutation(userId = userId, skillId = skillId, level = level)
                ).execute()

                if (response.hasErrors()) {
                    Log.e("SkillsViewModel", "GraphQL error adding skill: ${response.errors?.first()?.message}")
                    // Optionally set an error state
                } else {
                    // Success! Refresh the user's skills list to update the UI.
                    fetchUserSkills()
                }
            } catch (e: Exception) {
                Log.e("SkillsViewModel", "Network error adding skill: ${e.message}")
            }
        }
    }

    override fun removeUserSkill(userSkillId: String) {
        val userId = tokenManager.getUserId() ?: return
        viewModelScope.launch {
            try {
                // Assuming your generated mutation class is named this
                val response = apolloClient.mutation(
                    RemoveUserSkillMutation( userSkillId = userSkillId)
                ).execute()

                if (response.hasErrors()) {
                    Log.e("SkillsViewModel", "GraphQL error removing skill: ${response.errors?.first()?.message}")
                } else {
                    // Success! Refresh the list to update the UI.
                    fetchUserSkills()
                }
            } catch (e: Exception) {
                Log.e("SkillsViewModel", "Network error removing skill: ${e.message}")
            }
        }
    }
}