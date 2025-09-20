package com.example.pathfinder.ui.screens.fake

import com.example.pathfinder.model.ProjectUI
import com.example.pathfinder.viewmodel.IProjectsViewModel
import com.example.pathfinder.viewmodel.ProjectsUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FakeProjectsViewModel : IProjectsViewModel {

    override val uiState: StateFlow<ProjectsUiState> =
        MutableStateFlow(
            ProjectsUiState.Success(
                listOf(
                    ProjectUI(
                        projectId = "proj1",
                        name = "AI Data Platform (Preview)",
                        description = "A platform for data analysis.",
                        githubLink = "github.com/preview",
                        status = "ACTIVE"
                    ),
                    ProjectUI(
                        projectId = "proj2",
                        name = "Mobile App Redesign (Preview)",
                        description = "Redesigning the flagship mobile app.",
                        githubLink = "github.com/preview2",
                        status = "COMPLETED"
                    )
                )
            )
        )

    // The function does nothing in a static preview
    override fun fetchUserProjects() {}
}