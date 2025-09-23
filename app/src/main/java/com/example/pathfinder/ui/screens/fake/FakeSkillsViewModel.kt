package com.example.pathfinder.ui.screens.fake

import com.example.pathfinder.graphql.GetSkillsQuery
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

    // --- ADDED: Implementation for the new members ---
    override val allSkills: StateFlow<List<GetSkillsQuery.GetSkill>> =
        MutableStateFlow(emptyList()) // Provide an empty list for the preview

    override fun fetchUserSkills() {}

    override fun fetchAllSkills() {} // Does nothing in a static preview

    override fun addUserSkill(skillId: String, level: String) {} // Does nothing in a static preview
    // --- END OF ADDITIONS ---

    override fun removeUserSkill(userSkillId: String) {}
}