package com.example.pathfinder.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.api.Optional
import com.example.pathfinder.graphql.AddUserCareerGoalMutation
import com.example.pathfinder.graphql.AddUserSkillMutation
import com.example.pathfinder.graphql.GetCareerPathsQuery
import com.example.pathfinder.graphql.GetSkillsQuery
import com.example.pathfinder.graphql.GetUserOnboardingDataQuery
import com.example.pathfinder.graphql.RemoveUserCareerGoalMutation
import com.example.pathfinder.graphql.UpdateUserMutation
import com.example.pathfinder.model.CareerGoalUI
import com.example.pathfinder.model.ProjectUI
import com.example.pathfinder.model.UserSkillUI
import com.example.pathfinder.network.apolloClient
import com.example.pathfinder.network.data.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

sealed interface UiState {
    object Idle : UiState
    object Loading : UiState
    data class Success(val message: String) : UiState
    data class Error(val message: String) : UiState
}

interface IOnboardingViewModel {
    val name: StateFlow<String>
    val email: StateFlow<String>
    val currentRole: StateFlow<String>
    val yearsExperience: StateFlow<Int>
    val highestQualification: StateFlow<String>
    val userSkills: StateFlow<List<UserSkillUI>>
    val projects: StateFlow<List<ProjectUI>>
    val uiState: StateFlow<UiState>

    val allSkills: StateFlow<List<GetSkillsQuery.GetSkill>> // For the search results
    val skillSearchQuery: StateFlow<String>

    val allCareerPaths: StateFlow<List<GetCareerPathsQuery.GetCareerPath>>
    val careerSearchQuery: StateFlow<String>
    val userCareerGoals: StateFlow<List<CareerGoalUI>> // Assuming a UI model
    val longTermGoal: StateFlow<String>

    fun onNameChange(newName: String)
    fun onCurrentRoleChange(newRole: String)
    fun onYearsExperienceChange(newYears: Int)
    fun onHighestQualificationChange(newQualification: String)

    fun loadInitialData()
    fun saveBasicInfo()
    fun addUserSkill(skillId: String, level: String)

    fun onSkillSearchQueryChange(query: String)
    fun fetchAllSkills() // To populate the search
    fun onUserSkillChange(index: Int, updatedSkill: UserSkillUI)
    fun onAddNewSkillRow()
    fun removeUserSkill(userSkillId: String)

    fun onCareerSearchQueryChange(query: String)
    fun fetchAllCareerPaths()
    fun addUserCareerGoal(careerPathId: String)
    fun removeUserCareerGoal(careerPathId: String)
    fun onLongTermGoalChange(description: String)

    fun onProjectChange(index: Int, updatedProject: ProjectUI)
    fun onAddProject()
    fun submitFinalReview()
    // Add any other functions your UI needs to call
}

class OnboardingViewModel(application: Application) : AndroidViewModel(application), IOnboardingViewModel {
    private val tokenManager = TokenManager(application)

    // === STATE FOR THE ENTIRE FLOW ===
    override val name = MutableStateFlow("")
    override val email = MutableStateFlow("")
    override val currentRole = MutableStateFlow("")
    override val yearsExperience = MutableStateFlow(0) // Assuming Int
    override val highestQualification = MutableStateFlow("")
    override val userSkills = MutableStateFlow<List<UserSkillUI>>(emptyList())
    override val projects = MutableStateFlow<List<ProjectUI>>(emptyList())
    // ... add state for career goals, projects, etc.

    // State for loading/error messages
    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    override val uiState = _uiState.asStateFlow()

    override val allSkills = MutableStateFlow<List<GetSkillsQuery.GetSkill>>(emptyList())
    override val skillSearchQuery = MutableStateFlow("")

    override fun loadInitialData() {
        val userId = tokenManager.getUserId() ?: return
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                // Use the new comprehensive query
                val response = apolloClient.query(GetUserOnboardingDataQuery(userId)).execute()
                response.data?.getUserById?.let { user ->
                    // Populate all the state flows with the fetched data
                    name.value = user.name ?: ""
                    email.value = user.email
                    // ... populate all other states ...
                    userSkills.value = user.skills?.mapNotNull { it }?.map {
                        UserSkillUI(
                            userSkillId = it.id,
                            skillId = it.skill?.id ?: "",
                            name = it.skill?.name ?: "Unknown",
                            level = it.level ?: "Beginner"
                        )
                    } ?: emptyList()

                    projects.value = user.projects?.mapNotNull { it }?.map {
                        ProjectUI(
                            projectId = it.id,
                            name = it.name,
                            description = it.description ?: "",
                            githubLink = it.githubLink ?: "",
                            status = it.status.name 
                        )
                    } ?: emptyList()
                    _uiState.value = UiState.Success("Data Loaded")
                } ?: run { _uiState.value = UiState.Error("User not found") }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Network error")
            }
        }
    }

    override fun saveBasicInfo() {
        val userId = tokenManager.getUserId()
        if (userId == null) {
            _uiState.value = UiState.Error("Cannot save, user not logged in")
            return
        }

        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                // We use the generated UpdateUserMutation from our .graphql file
                val response = apolloClient.mutation(
                    UpdateUserMutation(
                        id = userId,
                        name = Optional.present(name.value)
                    )
                ).execute()

                if (response.hasErrors()) {
                    val error = response.errors?.firstOrNull()?.message ?: "An unknown error occurred"
                    _uiState.value = UiState.Error(error)
                } else {
                    _uiState.value = UiState.Success("Basic Info Saved!")
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "A network error occurred")
            }
        }
    }

    override fun addUserSkill(skillId: String, level: String) {
        val userId = tokenManager.getUserId()
        if (userId == null) {
            _uiState.value = UiState.Error("Cannot add skill, user not logged in")
            return
        }

        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val response = apolloClient.mutation(
                    AddUserSkillMutation(
                        userId = userId,
                        skillId = skillId,
                        level = level
                    )
                ).execute()

                if (response.hasErrors()) {
                    val error = response.errors?.firstOrNull()?.message ?: "Failed to add skill"
                    _uiState.value = UiState.Error(error)
                } else {
                    // Success! Now refresh all the user data to reflect the change.
                    Log.d("OnboardingViewModel", "Skill added successfully, refreshing user data.")
                    loadInitialData()
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "A network error occurred")
            }
        }
    }

    override fun onNameChange(newName: String) { name.value = newName }
    override fun onCurrentRoleChange(newRole: String) { currentRole.value = newRole }
    override fun onYearsExperienceChange(newYears: Int) { yearsExperience.value = newYears }
    override fun onHighestQualificationChange(newQualification: String) { highestQualification.value = newQualification }

    override fun onSkillSearchQueryChange(query: String) {
        skillSearchQuery.value = query
        // You can add logic here to filter `allSkills` based on the query
    }

    override fun fetchAllSkills() {
        viewModelScope.launch {
            try {
                // Assuming you have a GetSkillsQuery defined in a .graphql file
                val response = apolloClient.query(GetSkillsQuery()).execute()
                allSkills.value = response.data?.getSkills?.mapNotNull { it } ?: emptyList()
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Failed to fetch skills")
            }
        }
    }

    override fun onUserSkillChange(index: Int, updatedSkill: UserSkillUI) {
        val currentSkills = userSkills.value.toMutableList()
        if (index >= 0 && index < currentSkills.size) {
            currentSkills[index] = updatedSkill
            userSkills.value = currentSkills
        }
    }

    override fun onAddNewSkillRow() {
        val currentSkills = userSkills.value.toMutableList()
        // Add a new blank skill. The ID can be a temporary unique value.
        currentSkills.add(UserSkillUI(userSkillId = UUID.randomUUID().toString(), skillId = "", name = "", level = "Beginner"))
        userSkills.value = currentSkills
    }

    override fun removeUserSkill(userSkillId: String) {
        // Here you would call the RemoveUserSkillMutation
        // For now, we'll just update the local state
        val currentSkills = userSkills.value.toMutableList()
        currentSkills.removeAll { it.userSkillId == userSkillId }
        userSkills.value = currentSkills
        // In a real app, you would call the mutation and then refresh data with loadInitialData()
    }

    override val allCareerPaths = MutableStateFlow<List<GetCareerPathsQuery.GetCareerPath>>(emptyList())
    override val careerSearchQuery = MutableStateFlow("")
    override val userCareerGoals = MutableStateFlow<List<CareerGoalUI>>(emptyList())
    override val longTermGoal = MutableStateFlow("")

    override fun onCareerSearchQueryChange(query: String) {
        careerSearchQuery.value = query
    }

    override fun fetchAllCareerPaths() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                // Use the newly generated query class
                val response = apolloClient.query(GetCareerPathsQuery()).execute()
                val paths = response.data?.getCareerPaths

                if (paths != null && !response.hasErrors()) {
                    allCareerPaths.value = paths.mapNotNull { it }
                    _uiState.value = UiState.Idle // Go back to idle after loading
                } else {
                    val error = response.errors?.firstOrNull()?.message ?: "Failed to load career paths"
                    _uiState.value = UiState.Error(error)
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Network error")
            }
        }
    }

    override fun addUserCareerGoal(careerPathId: String) {
        val userId = tokenManager.getUserId()
        if (userId == null) {
            _uiState.value = UiState.Error("Cannot add career goal, user not logged in")
            return
        }

        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val response = apolloClient.mutation(
                    AddUserCareerGoalMutation(
                        userId = userId,
                        careerPathId = careerPathId
                    )
                ).execute()

                if (response.hasErrors()) {
                    val error = response.errors?.firstOrNull()?.message ?: "Failed to add career goal"
                    _uiState.value = UiState.Error(error)
                } else {
                    // Success! Refresh all user data to show the new goal.
                    Log.d("OnboardingViewModel", "Career goal added, refreshing data.")
                    loadInitialData()
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "A network error occurred")
            }
        }
    }

    override fun removeUserCareerGoal(careerPathId: String) {
        val userId = tokenManager.getUserId()
        if (userId == null) {
            _uiState.value = UiState.Error("Cannot remove career goal, user not logged in")
            return
        }

        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val response = apolloClient.mutation(
                    RemoveUserCareerGoalMutation(
                        userId = userId,
                        careerPathId = careerPathId
                    )
                ).execute()

                if (response.hasErrors()) {
                    val error = response.errors?.firstOrNull()?.message ?: "Failed to remove career goal"
                    _uiState.value = UiState.Error(error)
                } else {
                    // Success! Refresh all user data to remove the goal from the UI.
                    Log.d("OnboardingViewModel", "Career goal removed, refreshing data.")
                    loadInitialData()
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "A network error occurred")
            }
        }
    }

    override fun onLongTermGoalChange(description: String) {
        longTermGoal.value = description
    }

    override fun onProjectChange(index: Int, updatedProject: ProjectUI) {
        val currentProjects = projects.value.toMutableList()
        if (index in currentProjects.indices) {
            currentProjects[index] = updatedProject
            projects.value = currentProjects
        }
    }

    override fun onAddProject() {
        val currentProjects = projects.value.toMutableList()
        // Add a new blank project with a temporary unique ID
        currentProjects.add(ProjectUI(
            projectId = UUID.randomUUID().toString(), name = "", description = "", githubLink = "",
            status = ""
        ))
        projects.value = currentProjects
    }

    override fun submitFinalReview() {
        // Here, you would call all the necessary mutations to save any unsaved data
        // from the entire flow (e.g., save basic info, add/update skills, save projects).
        // For now, we'll just set a success state.
        _uiState.value = UiState.Success("Profile Submitted Successfully!")
    }
}