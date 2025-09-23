package com.example.pathfinder.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.pathfinder.model.UserSkillUI
import com.example.pathfinder.ui.screens.fake.FakeSkillsViewModel
import com.example.pathfinder.ui.theme.PathfinderAITheme
import com.example.pathfinder.viewmodel.ISkillsViewModel
import com.example.pathfinder.viewmodel.SkillsUiState
import com.example.pathfinder.viewmodel.SkillsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkillsTabScreen(
    navController: NavController,
    viewModel: ISkillsViewModel = viewModel<SkillsViewModel>() // Use the new ViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val allSkills by viewModel.allSkills.collectAsStateWithLifecycle()
    var showAddSkillDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.fetchUserSkills()
        viewModel.fetchAllSkills() // Fetch all skills for the dialog
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Skills Tab") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddSkillDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Skill")
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { paddingValues ->
        when (val state = uiState) {
            is SkillsUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is SkillsUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            is SkillsUiState.Success -> {
                if (state.userSkills.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No skills added yet. Tap '+' to add your first skill!",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.userSkills) { skill ->
                            SkillItemCard(
                                skill = skill,
                                onClick = {
                                    // TODO: Navigate to skill details/edit screen
                                },
                                // Pass the remove function from the ViewModel
                                onRemove = { viewModel.removeUserSkill(skill.userSkillId) }
                            )
                        }
                    }
                }
            }
        }

        // Show the dialog when the state is true
        if (showAddSkillDialog) {
            AddSkillDialog(
                allSkills = allSkills,
                onDismiss = { showAddSkillDialog = false },
                onConfirm = { skillId, level ->
                    viewModel.addUserSkill(skillId, level)
                    showAddSkillDialog = false
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkillItemCard(
    skill: UserSkillUI,
    onClick: () -> Unit,
    onRemove: () -> Unit // 1. Add the onRemove parameter
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp),
        onClick = onClick // The card is still clickable for navigation
    ) {
        Row(
            modifier = Modifier
                .padding(start = 16.dp, end = 8.dp, top = 16.dp, bottom = 16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = skill.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = skill.level,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            // 2. Add the delete button
            IconButton(onClick = onRemove) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Remove Skill",
                    tint = MaterialTheme.colorScheme.error
                )
            }
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "View Details",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSkillsTabScreen() {
    PathfinderAITheme {
        SkillsTabScreen(navController = rememberNavController(), viewModel = FakeSkillsViewModel())
    }
}