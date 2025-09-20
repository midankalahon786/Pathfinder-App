package com.example.pathfinder.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.api.Optional
import com.example.pathfinder.graphql.GetUserOnboardingDataQuery
import com.example.pathfinder.graphql.UpdateUserMutation
import com.example.pathfinder.graphql.type.Gender
import com.example.pathfinder.network.apolloClient
import com.example.pathfinder.network.data.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// UI states for loading/success/error feedback
sealed interface ProfileUiState {
    object Idle : ProfileUiState
    object Loading : ProfileUiState
    data class Success(val message: String) : ProfileUiState
    data class Error(val message: String) : ProfileUiState
}

// --- UPDATED INTERFACE ---
interface IProfileViewModel {
    val name: StateFlow<String>
    val phone: StateFlow<String>
    val birthday: StateFlow<String>
    val email: StateFlow<String>
    val gender: StateFlow<Gender?>
    val profileImageUrl: StateFlow<String?>
    val uiState: StateFlow<ProfileUiState>

    fun onNameChange(newName: String)
    fun onPhoneChange(newPhone: String)
    fun onBirthdayChange(newBirthday: String)
    fun onGenderChange(newGender: Gender)
    fun fetchProfile()
    fun saveChanges()
    fun resetUiState()
}

// --- UPDATED CLASS ---
class ProfileViewModel(application: Application) : AndroidViewModel(application), IProfileViewModel {
    private val tokenManager = TokenManager(application)

    // State for each editable field
    override val name = MutableStateFlow("")
    override val phone = MutableStateFlow("")
    override val birthday = MutableStateFlow("")
    override val email = MutableStateFlow("") // Email is not editable
    override val gender = MutableStateFlow<Gender?>(null)
    override val profileImageUrl = MutableStateFlow<String?>(null)

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Idle)
    override val uiState = _uiState.asStateFlow()

    override fun fetchProfile() {
        val userId = tokenManager.getUserId()
        if (userId == null) {
            _uiState.value = ProfileUiState.Error("User not logged in")
            return
        }

        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            try {
                // Assuming you use the comprehensive onboarding query here as well
                val response = apolloClient.query(GetUserOnboardingDataQuery(id = userId)).execute()
                response.data?.getUserById?.let { user ->
                    name.value = user.name ?: ""
                    phone.value = user.phone ?: ""
                    birthday.value = user.birthday ?: ""
                    email.value = user.email
                    gender.value = user.gender
                    profileImageUrl.value = user.profileImageUrl
                    _uiState.value = ProfileUiState.Idle // Ready after loading
                } ?: run {
                    _uiState.value = ProfileUiState.Error("User not found")
                }
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error(e.message ?: "Failed to fetch profile")
            }
        }
    }

    override fun onNameChange(newName: String) {
        name.value = newName
    }

    override fun onPhoneChange(newPhone: String) {
        phone.value = newPhone
    }

    override fun onBirthdayChange(newBirthday: String) {
        birthday.value = newBirthday
    }

    override fun onGenderChange(newGender: Gender) {
        gender.value = newGender
    }

    override fun saveChanges() {
        val userId = tokenManager.getUserId()
        if (userId == null) {
            _uiState.value = ProfileUiState.Error("Cannot save, user not logged in")
            return
        }

        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            try {
                // Note: Ensure your UpdateUserMutation in .graphql file accepts these new fields
                val response = apolloClient.mutation(
                    UpdateUserMutation(
                        id = userId,
                        name = Optional.presentIfNotNull(name.value),
                        phone = Optional.presentIfNotNull(phone.value),
                        birthday = Optional.presentIfNotNull(birthday.value),
                        gender = Optional.presentIfNotNull(gender.value),
                        profileImageUrl = Optional.presentIfNotNull(profileImageUrl.value)
                    )
                ).execute()

                if (response.hasErrors()) {
                    val error = response.errors?.firstOrNull()?.message ?: "Save failed"
                    _uiState.value = ProfileUiState.Error(error)
                } else {
                    _uiState.value = ProfileUiState.Success("Changes Saved!")
                }
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error(e.message ?: "Network error during save")
            }
        }
    }

    override fun resetUiState() {
        _uiState.value = ProfileUiState.Idle
    }
}
