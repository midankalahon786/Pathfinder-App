package com.example.pathfinder.ui.screens.fake

import com.example.pathfinder.graphql.GetCareerPathsQuery
import com.example.pathfinder.graphql.GetSkillsQuery
import com.example.pathfinder.model.CareerGoalUI
import com.example.pathfinder.model.ProjectUI
import com.example.pathfinder.model.UserSkillUI
import com.example.pathfinder.viewmodel.IOnboardingViewModel
import com.example.pathfinder.viewmodel.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FakeOnboardingViewModel : IOnboardingViewModel {

    override val name: StateFlow<String> =
        MutableStateFlow("James Harried (Preview)")

    override val email: StateFlow<String> =
        MutableStateFlow("james.preview@email.com")

    override val currentRole: StateFlow<String> =
        MutableStateFlow("Software Engineer")

    override val yearsExperience: StateFlow<Int> =
        MutableStateFlow(5)

    override val highestQualification: StateFlow<String> =
        MutableStateFlow("B.Tech CSE")

    override val userSkills: StateFlow<List<UserSkillUI>> =
        MutableStateFlow(
            listOf(
                UserSkillUI("uSkill1", "skill1", "Kotlin", "Expert"),
                UserSkillUI("uSkill2", "skill2", "Jetpack Compose", "Intermediate")
            )
        )

    override val projects: StateFlow<List<ProjectUI>> =
        MutableStateFlow(
            listOf(
                ProjectUI("proj1", "Pathfinder App", "A career guidance app.", "github.com/pathfinder", status = "ACTIVE")
            )
        )

    override val uiState: StateFlow<UiState> =
        MutableStateFlow(UiState.Idle)

    override val allSkills: StateFlow<List<GetSkillsQuery.GetSkill>> =
        MutableStateFlow(
            // Provide a few sample skills for the search preview
            listOf(
                GetSkillsQuery.GetSkill("skill1", "Kotlin", "Language"),
                GetSkillsQuery.GetSkill("skill2", "Jetpack Compose", "UI Toolkit"),
                GetSkillsQuery.GetSkill("skill3", "Android SDK", "Platform")
            )
        )

    override val skillSearchQuery: StateFlow<String> =
        MutableStateFlow("")

    override val allCareerPaths: StateFlow<List<GetCareerPathsQuery.GetCareerPath>> =
        MutableStateFlow(
            listOf(
                GetCareerPathsQuery.GetCareerPath("path1", "AI & Machine Learning", null),
                GetCareerPathsQuery.GetCareerPath("path2", "Cloud Computing", null),
                GetCareerPathsQuery.GetCareerPath("path3", "Cyber Security", null)
            )
        )

    override val careerSearchQuery: StateFlow<String> = MutableStateFlow("")

    override val userCareerGoals: StateFlow<List<CareerGoalUI>> = MutableStateFlow(
        listOf(
            CareerGoalUI("path1", "AI & Machine Learning"),
            CareerGoalUI("path2", "Cloud Computing")
        )
    )
    override val longTermGoal: StateFlow<String> =
        MutableStateFlow("Become a principal engineer in the AI/ML space.")


    override fun onNameChange(newName: String) {}
    override fun onCurrentRoleChange(newRole: String) {}
    override fun onYearsExperienceChange(newYears: Int) {}
    override fun onHighestQualificationChange(newQualification: String) {}

    override fun loadInitialData() {}
    override fun saveBasicInfo() {}
    override fun addUserSkill(skillId: String, level: String) {}

    override fun onSkillSearchQueryChange(query: String) {}
    override fun fetchAllSkills() {}
    override fun onUserSkillChange(index: Int, updatedSkill: UserSkillUI) {}
    override fun onAddNewSkillRow() {}
    override fun removeUserSkill(userSkillId: String) {}

    override fun onCareerSearchQueryChange(query: String) {}
    override fun fetchAllCareerPaths() {}
    override fun addUserCareerGoal(careerPathId: String) {}
    override fun removeUserCareerGoal(careerPathId: String) {}
    override fun onLongTermGoalChange(description: String) {}

    override fun onProjectChange(index: Int, updatedProject: ProjectUI) {}
    override fun onAddProject() {}
    override fun removeProject(projectId: String) {}
    override fun submitFinalReview() {}
}