package com.example.pathfinder.ui.screens.fake

import com.example.pathfinder.viewmodel.AuthState
import com.example.pathfinder.viewmodel.IAuthViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FakeAuthViewModel : IAuthViewModel {
    override val authState: StateFlow<AuthState> = MutableStateFlow(AuthState.Idle)
    override fun register(email: String, password: String, name: String) {} // Do nothing
    override fun login(email: String, password: String) {} // Do nothing
    override fun logout() {} // Do nothing
    override fun resetState() {} // Do nothing
}