package com.example.pathfinder.ui.screens.fake

import com.example.pathfinder.model.SkillUI
import com.example.pathfinder.viewmodel.HomeUiState
import com.example.pathfinder.viewmodel.IHomeViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * A fake implementation of the IHomeViewModel for use in Jetpack Compose previews.
 * It provides a hardcoded list of trending skills in a success state.
 */
class FakeHomeViewModel : IHomeViewModel {

    override val uiState: StateFlow<HomeUiState> =
        MutableStateFlow(
            HomeUiState.Success(
                listOf(
                    SkillUI("skill1", "Jetpack Compose", "Android"),
                    SkillUI("skill2", "Kotlin Multiplatform", "Mobile"),
                    SkillUI("skill3", "AI Development", "Machine Learning")
                ),

            )
        )

    // The function does nothing in a static preview
    override fun fetchTrendingSkills() {}
}
