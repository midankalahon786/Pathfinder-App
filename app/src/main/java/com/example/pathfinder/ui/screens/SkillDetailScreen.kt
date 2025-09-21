package com.example.pathfinder.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.pathfinder.R
import com.example.pathfinder.ui.screens.fake.FakeSkillDetailViewModel
import com.example.pathfinder.ui.theme.TealHeader
import com.example.pathfinder.viewmodel.ISkillDetailViewModel
import com.example.pathfinder.viewmodel.SkillDetailState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkillDetailScreen(
    navController: NavController,
    skillName: String,
    viewModel: ISkillDetailViewModel
) {
    // Collect the UI state from the ViewModel
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Fetch the skill details when the screen is first composed or skillName changes
    LaunchedEffect(key1 = skillName) {
        viewModel.fetchSkillDetails(skillName)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(skillName) }, // Use the passed-in skillName for an immediate title
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        // Handle the different UI states
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is SkillDetailState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is SkillDetailState.Error -> {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is SkillDetailState.Success -> {
                    val skill = state.skill
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Header Section
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = skill.name,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = skill.description,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { /* TODO: Handle add to my skills */ },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = TealHeader,
                                    contentColor = Color.White
                                )
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.baseline_bookmark_border_24),
                                    contentDescription = null,
                                    modifier = Modifier.size(ButtonDefaults.IconSize)
                                )
                                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                                Text("Add to My Skills")
                            }
                            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                        }

                        // Related Roles Section
                        if (skill.relatedRoles.isNotEmpty()) {
                            item {
                                Text(
                                    "Related Roles",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                            items(skill.relatedRoles) { roleTitle ->
                                Text(
                                    text = "• $roleTitle",
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
                                )
                            }
                            item {
                                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                            }
                        }

                        // Courses Section
                        if (skill.courses.isNotEmpty()) {
                            item {
                                Text(
                                    "Courses to Learn",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                            items(skill.courses) { course ->
                                CourseCard(course = course)
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }
}

// Update CourseCard to take the Pair from the ViewModel's UI model
@Composable
fun CourseCard(course: Pair<String, String>) {
    val (title, provider) = course
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.baseline_school_24),
                contentDescription = null,
                tint = TealHeader,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = provider,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun SkillDetailScreenPreview() {
    MaterialTheme {
        SkillDetailScreen(
            navController = rememberNavController(),
            skillName = "Jetpack Compose",
            viewModel = FakeSkillDetailViewModel()
        )
    }
}