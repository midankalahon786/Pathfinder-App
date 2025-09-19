package com.example.pathfinder.ui.screens.fake

import com.example.pathfinder.graphql.GetUserByIdQuery
import com.example.pathfinder.viewmodel.IUserViewModel
import com.example.pathfinder.viewmodel.UserState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FakeUserViewModel : IUserViewModel {
    // Create a fake user object to display in the preview
    private val fakeUser = GetUserByIdQuery.GetUserById(
        id = "preview-user-123",
        name = "Anthony Moore",
        email = "anthony.preview@example.com",
        // Add fake data for the other required fields
        currentRole = "Software Engineer (Preview)",
        skills = listOf(
            GetUserByIdQuery.Skill(
                skill = GetUserByIdQuery.Skill1(id = "skill-1", name = "Android", category = "Framework"),
                level = "Expert"
            ),
            GetUserByIdQuery.Skill(
                skill = GetUserByIdQuery.Skill1(id = "skill-2", name = "Kotlin", category = "Language"),
                level = "Expert"
            ),
            GetUserByIdQuery.Skill(
                skill = GetUserByIdQuery.Skill1(id = "skill-3", name = "Compose", category = "UI Toolkit"),
                level = "Intermediate"
            )
        ),
        yearsExperience = 5,
        phone = "123-456-7890" ,
        birthday = "01/01/2000" ,
    )

    override val userState: StateFlow<UserState> = MutableStateFlow(UserState.Success(fakeUser))
    override fun fetchCurrentUser() {}
}