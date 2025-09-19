package com.example.pathfinder.viewmodel

import com.example.pathfinder.graphql.GetUserByIdQuery
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.api.Optional
import com.example.pathfinder.graphql.UpdateUserMutation
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
    object Success : ProfileUiState
    data class Error(val message: String) : ProfileUiState
}
interface IProfileViewModel {
    val name: StateFlow<String>
    val phone: StateFlow<String>
    val birthday: StateFlow<String>
    val email: StateFlow<String>
    val uiState: StateFlow<ProfileUiState>

    fun onNameChange(newName: String)
    fun onPhoneChange(newPhone: String)
    fun onBirthdayChange(newBirthday: String)
    fun fetchProfile()
    fun saveChanges()
    fun resetUiState()
}

class ProfileViewModel(application: Application) : AndroidViewModel(application), IProfileViewModel {
    private val tokenManager = TokenManager(application)

    // State for each editable field
    override val name = MutableStateFlow("")
    override val phone = MutableStateFlow("")
    override val birthday = MutableStateFlow("")
    override val email = MutableStateFlow("") // Email is not editable

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
                val response = apolloClient.query(GetUserByIdQuery(id = userId)).execute()
                response.data?.getUserById?.let { user ->
                    name.value = user.name ?: ""
                    phone.value = user.phone ?: "" // Assuming you add phone to your schema/query
                    birthday.value = user.birthday ?: "" // Assuming you add birthday
                    email.value = user.email
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

    override fun saveChanges() {
        val userId = tokenManager.getUserId()
        if (userId == null) {
            _uiState.value = ProfileUiState.Error("Cannot save, user not logged in")
            return
        }

        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            try {
                val response = apolloClient.mutation(
                    UpdateUserMutation(
                        id = userId,
                        name = Optional.presentIfNotNull(name.value),
                        phone = Optional.presentIfNotNull(phone.value),
                        birthday = Optional.presentIfNotNull(birthday.value)
                    )
                ).execute()

                if (response.hasErrors()) {
                    _uiState.value = ProfileUiState.Error(response.errors?.firstOrNull()?.message ?: "Save failed")
                } else {
                    _uiState.value = ProfileUiState.Success
                }
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error(e.message ?: "Network error during save")
            }
        }
    }

    // To reset the state after showing a message
    override fun resetUiState() {
        _uiState.value = ProfileUiState.Idle
    }
}