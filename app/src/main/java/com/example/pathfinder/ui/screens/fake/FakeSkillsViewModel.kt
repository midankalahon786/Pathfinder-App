package com.example.pathfinder.ui.screens.fake

import com.example.pathfinder.model.UserSkillUI
import com.example.pathfinder.viewmodel.ISkillsViewModel
import com.example.pathfinder.viewmodel.SkillsUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * A fake implementation of the ISkillsViewModel for use in Jetpack Compose previews.
 * It provides a hardcoded list of skills in a success state.
 */
class FakeSkillsViewModel : ISkillsViewModel {

    override val uiState: StateFlow<SkillsUiState> =
        MutableStateFlow(
            SkillsUiState.Success(
                listOf(
                    UserSkillUI("uskill1", "skill1", "Jetpack Compose", "Advanced"),
                    UserSkillUI("uskill2", "skill2", "Kotlin", "Expert"),
                    UserSkillUI("uskill3", "skill3", "RESTful APIs", "Intermediate")
                )
            )
        )

    // The function does nothing in a static preview
    override fun fetchUserSkills() {}
}