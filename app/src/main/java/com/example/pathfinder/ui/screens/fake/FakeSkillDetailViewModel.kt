package com.example.pathfinder.ui.screens.fake

import com.example.pathfinder.model.SkillDetailUI
import com.example.pathfinder.viewmodel.ISkillDetailViewModel
import com.example.pathfinder.viewmodel.SkillDetailState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * A fake implementation of the ISkillDetailViewModel for use in Jetpack Compose previews.
 * It provides a hardcoded skill detail object in a success state.
 */
class FakeSkillDetailViewModel : ISkillDetailViewModel {

    // Create a sample SkillDetailUI object with realistic data for the preview
    private val fakeSkillData = SkillDetailUI(
        id = "preview_skill_1",
        name = "Jetpack Compose",
        description = "Jetpack Compose is Android’s recommended modern toolkit for building native UI. It simplifies and accelerates UI development on Android with less code, powerful tools, and intuitive Kotlin APIs.",
        relatedRoles = listOf(
            "Android Developer",
            "Mobile Engineer",
            "Kotlin Developer"
        ),
        courses = listOf(
            "Jetpack Compose for Android Developers" to "Udacity",
            "Android Basics with Compose" to "Google",
            "Complete Jetpack Compose Masterclass" to "Udemy"
        )
    )

    override val uiState: StateFlow<SkillDetailState> =
        MutableStateFlow(SkillDetailState.Success(fakeSkillData))

    // The function does nothing in a static preview
    override fun fetchSkillDetails(skillName: String) {}
}

