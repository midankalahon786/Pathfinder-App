package com.example.pathfinder.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pathfinder.graphql.GetUserByIdQuery
import com.example.pathfinder.network.apolloClient
import com.example.pathfinder.network.data.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

interface IUserViewModel {
    val userState: StateFlow<UserState>
    fun fetchCurrentUser()
}

class UserViewModel(application: Application) : AndroidViewModel(application), IUserViewModel {
    private val tokenManager = TokenManager(application)

    private val _userState = MutableStateFlow<UserState>(UserState.Loading)
    override val userState: StateFlow<UserState> = _userState

    override fun fetchCurrentUser() {
        val userId = tokenManager.getUserId()
        Log.d("UserViewModel", "Attempting to fetch user. Found userId: $userId")
        if (userId == null) {
            _userState.value = UserState.Error("User not logged in")
            return
        }

        viewModelScope.launch {
            _userState.value = UserState.Loading
            try {
                val response = apolloClient.query(GetUserByIdQuery(id = userId)).execute()
                if (response.data != null && !response.hasErrors()) {
                    _userState.value = UserState.Success(response.data!!.getUserById)
                } else {
                    _userState.value = UserState.Error(response.errors?.firstOrNull()?.message ?: "Unknown error")
                }
            } catch (e: Exception) {
                _userState.value = UserState.Error(e.message ?: "Network error")
            }
        }
    }
}

sealed interface UserState {
    object Loading : UserState
    data class Success(val user: GetUserByIdQuery.GetUserById?) : UserState
    data class Error(val message: String) : UserState
}
