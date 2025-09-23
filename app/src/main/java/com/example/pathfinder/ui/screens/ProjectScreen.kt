// File: ui/screens/ProjectsScreen.kt
package com.example.pathfinder.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Code
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.pathfinder.model.ProjectUI
import com.example.pathfinder.ui.screens.fake.FakeProjectsViewModel
import com.example.pathfinder.ui.theme.PathfinderAITheme // Import your app's theme
import com.example.pathfinder.viewmodel.IProjectsViewModel
import com.example.pathfinder.viewmodel.ProjectsUiState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectsScreen(
    navController: NavController,
    viewModel: IProjectsViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.fetchUserProjects()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Projects", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                // THEME: Use theme surface color for app bar
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO: Navigate to Add Project Screen */ },
                // THEME: Use theme primary color for FAB
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape
                // SIZE: Removed explicit size to use Material's default (56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Project",
                    // SIZE: Reduced icon size to Material's default (24.dp)
                )
            }
        },
        // THEME: Use theme background color
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // FilterTabs() // Assuming this will be implemented later
            Spacer(modifier = Modifier.height(16.dp))

            when (val state = uiState) {
                is ProjectsUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is ProjectsUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                is ProjectsUiState.Success -> {
                    if (state.projects.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                "No projects added yet. Tap '+' to add your first project!",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        ProjectList(projects = state.projects)
                    }
                }
            }
        }
    }
}

@Composable
fun FilterTabs() {
    // A placeholder for filter tabs
}

@Composable
fun ProjectList(projects: List<ProjectUI>) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(projects) { project ->
            ProjectItem(project = project)
        }
    }
}

@Composable
fun ProjectItem(project: ProjectUI) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        // SIZE: Reduced corner radius for a more standard look
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        // THEME: Use theme surface color for Card
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    // THEME: Use primaryContainer for icon background
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Code,
                    contentDescription = project.name,
                    // THEME: Use onPrimaryContainer for icon tint
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = project.name,
                    // SIZE: Use theme typography for consistency
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = project.description,
                    style = MaterialTheme.typography.bodyMedium,
                    // THEME: Use onSurfaceVariant for secondary text
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Go to project",
                // THEME: Use onSurfaceVariant for decorative icons
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProjectsScreenPreview() {
    // THEME: Wrap your preview in your app's theme to see the changes
    PathfinderAITheme {
        ProjectsScreen(
            navController = rememberNavController(),
            viewModel = FakeProjectsViewModel()
        )
    }
}