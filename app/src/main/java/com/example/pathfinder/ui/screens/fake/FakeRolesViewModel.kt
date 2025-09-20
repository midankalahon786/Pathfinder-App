package com.example.pathfinder.ui.screens.fake

import com.example.pathfinder.model.RoleUI
import com.example.pathfinder.viewmodel.IRolesViewModel
import com.example.pathfinder.viewmodel.RolesUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * A fake implementation of the IRolesViewModel for use in Jetpack Compose previews.
 * It provides a hardcoded list of roles in a success state.
 */
class FakeRolesViewModel : IRolesViewModel {

    override val uiState: StateFlow<RolesUiState> =
        MutableStateFlow(
            RolesUiState.Success(
                listOf(
                    RoleUI(title = "Software Engineer", isDesired = false),
                    RoleUI(title = "Tech Lead (Desired)", isDesired = true),
                    RoleUI(title = "Product Manager (Desired)", isDesired = true)
                )
            )
        )

    // The function does nothing in a static preview
    override fun fetchUserRoles() {}
}