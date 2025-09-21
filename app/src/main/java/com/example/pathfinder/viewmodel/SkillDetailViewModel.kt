package com.example.pathfinder.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pathfinder.graphql.GetSkillDetailsByNameQuery
import com.example.pathfinder.model.SkillDetailUI
import com.example.pathfinder.network.apolloClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

interface ISkillDetailViewModel {
    /**
     * The state of the UI, representing loading, success, or error states.
     */
    val uiState: StateFlow<SkillDetailState>

    /**
     * Fetches the details for a specific skill by its name.
     * @param skillName The name of the skill to fetch.
     */
    fun fetchSkillDetails(skillName: String)
}

sealed interface SkillDetailState {
    object Loading : SkillDetailState
    data class Success(val skill: SkillDetailUI) : SkillDetailState
    data class Error(val message: String) : SkillDetailState
}

// FIX: Inherit from AndroidViewModel and implement the interface
class SkillDetailViewModel(application: Application) : AndroidViewModel(application), ISkillDetailViewModel {

    private val _uiState = MutableStateFlow<SkillDetailState>(SkillDetailState.Loading)
    override val uiState: StateFlow<SkillDetailState> = _uiState.asStateFlow()

    override fun fetchSkillDetails(skillName: String) {
        viewModelScope.launch {
            _uiState.value = SkillDetailState.Loading
            try {
                val response = apolloClient.query(GetSkillDetailsByNameQuery(skillName)).execute()
                response.data?.getSkillByName?.let { skill ->
                    val uiData = SkillDetailUI(
                        id = skill.id,
                        name = skill.name,
                        description = skill.description ?: "No description available.",
                        relatedRoles = skill.relatedRoles?.mapNotNull { it?.title } ?: emptyList(),
                        courses = skill.courses?.mapNotNull { Pair(it?.title ?: "", it?.provider ?: "") } ?: emptyList()
                    )
                    _uiState.value = SkillDetailState.Success(uiData)
                } ?: run {
                    _uiState.value = SkillDetailState.Error("Skill not found.")
                }
            } catch (e: Exception) {
                _uiState.value = SkillDetailState.Error(e.message ?: "Network error.")
            }
        }
    }
}
