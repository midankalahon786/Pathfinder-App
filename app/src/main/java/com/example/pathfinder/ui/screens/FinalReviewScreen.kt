package com.example.pathfinder.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.pathfinder.model.ProjectUI
import com.example.pathfinder.ui.screens.fake.FakeOnboardingViewModel
import com.example.pathfinder.ui.theme.PathfinderAITheme
import com.example.pathfinder.viewmodel.IOnboardingViewModel
import com.example.pathfinder.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinalReviewScreen(
    navController: NavController,
    viewModel: IOnboardingViewModel
) {
    val projectsList by viewModel.projects.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState) {
        if (uiState is UiState.Success) {
            navController.navigate(Screen.SubmissionWelcome.route) {
                popUpTo(Screen.BasicInfo.route) { inclusive = true }
            }
        }
    }


    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Past Projects & Final Review") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Key Accomplishments",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Briefly describe one or two key projects or achievements. (Optional)",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))

            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                projectsList.forEachIndexed { index, project ->
                    ProjectInputCard(
                        project = project,
                        onProjectChange = { updatedProject ->
                            viewModel.onProjectChange(index, updatedProject)
                        },
                        projectNumber = index + 1,
                        // 3. Call the ViewModel's removeProject function
                        onRemove = { viewModel.removeProject(project.projectId) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = { viewModel.onAddProject() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add More")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add More")
            }

            Spacer(modifier = Modifier.weight(1f))

            OutlinedButton(
                onClick = { navController.navigateUp() },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                Text("Career Goals & Ambitions", modifier = Modifier.padding(horizontal = 8.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.submitFinalReview() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                enabled = uiState !is UiState.Loading
            ) {
                if (uiState is UiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Submit", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
@Composable
fun ProjectInputCard(
    project: ProjectUI,
    onProjectChange: (ProjectUI) -> Unit,
    projectNumber: Int,
    onRemove: () -> Unit // 1. Add the onRemove parameter
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 2. Place the Project Name and Delete Icon in a Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = project.name,
                    onValueChange = { onProjectChange(project.copy(name = it)) },
                    label = { Text("Project Name") },
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onRemove) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Remove Project",
                        tint = MaterialTheme.colorScheme.error // Use error color for delete actions
                    )
                }
            }

            OutlinedTextField(
                value = project.description,
                onValueChange = { onProjectChange(project.copy(description = it)) },
                label = { Text("Project $projectNumber Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )
            OutlinedTextField(
                value = project.githubLink,
                onValueChange = { onProjectChange(project.copy(githubLink = it)) },
                label = { Text("Github") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FinalReviewScreenPreview() {
    PathfinderAITheme {
        FinalReviewScreen(navController = rememberNavController(), viewModel = FakeOnboardingViewModel())
    }
}