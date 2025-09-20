package com.example.pathfinder.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
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
import com.example.pathfinder.ui.theme.DarkBlueText
import com.example.pathfinder.ui.theme.DividerColor
import com.example.pathfinder.ui.theme.LightPurpleBackground
import com.example.pathfinder.ui.theme.MediumGrayText
import com.example.pathfinder.ui.theme.TealHeader
import com.example.pathfinder.viewmodel.IOnboardingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinalReviewScreen(
    navController: NavController,
    viewModel: IOnboardingViewModel
) {
    val projectsList by viewModel.projects.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = LightPurpleBackground,
        topBar = {
            TopAppBar(
                title = { Text("Past Projects & Final Review", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = TealHeader)
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
                color = DarkBlueText
            )
            Text(
                text = "Briefly describe one or two key projects or achievements. (Optional)",
                style = MaterialTheme.typography.bodyMedium,
                color = MediumGrayText,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))

            // A column to hold all the dynamic project cards
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                projectsList.forEachIndexed { index, project ->
                    ProjectInputCard(
                        project = project,
                        onProjectChange = { updatedProject ->
                            viewModel.onProjectChange(index, updatedProject)
                        },
                        projectNumber = index + 1
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // "Add More" Button with dashed border
            Stroke(
                width = 2f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
            )
            Button(
                onClick = { viewModel.onAddProject() },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = MediumGrayText
                ),
                border = BorderStroke(1.dp, DividerColor)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add More")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add More")
            }

            Spacer(modifier = Modifier.weight(1f))

            // Navigation Buttons
            Button(
                onClick = { navController.navigateUp() },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = MediumGrayText
                ),
                border = BorderStroke(1.dp, DividerColor)
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
                colors = ButtonDefaults.buttonColors(containerColor = TealHeader)
            ) {
                Text("Submit", fontSize = 16.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ProjectInputCard(
    project: ProjectUI, // Use the UI model
    onProjectChange: (ProjectUI) -> Unit,
    projectNumber: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = project.name,
                onValueChange = { onProjectChange(project.copy(name = it)) },
                label = { Text("Project Name") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = project.description,
                onValueChange = { onProjectChange(project.copy(description = it)) },
                label = { Text("Project $projectNumber Description") },
                modifier = Modifier.fillMaxWidth().height(120.dp)
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
    MaterialTheme {
        FinalReviewScreen(navController = rememberNavController(), viewModel = FakeOnboardingViewModel())
    }
}