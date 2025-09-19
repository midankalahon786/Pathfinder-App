package com.example.pathfinder.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pathfinder.graphql.GetUserByIdQuery
import com.example.pathfinder.network.apolloClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {

    private val _userState = MutableStateFlow<UserState>(UserState.Loading)
    val userState: StateFlow<UserState> = _userState

    fun fetchUser(userId: String) {
        viewModelScope.launch {
            _userState.value = UserState.Loading
            try {
                val response = apolloClient.query(GetUserByIdQuery(id = userId)).execute()
                if (response.data != null && !response.hasErrors()) {
                    _userState.value = UserState.Success(response.data!!.getUserById)
                } else {
                    _userState.value = UserState.Error(response.errors?.firstOrNull()?.message ?: "Unknown GraphQL error")
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
