// File: ui/screens/RolesTitlesScreen.kt
package com.example.pathfinder.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
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
import com.example.pathfinder.model.RoleUI
import com.example.pathfinder.ui.screens.fake.FakeRolesViewModel
import com.example.pathfinder.viewmodel.IRolesViewModel
import com.example.pathfinder.viewmodel.RolesUiState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RolesTitlesScreen(
    navController: NavController,
    viewModel: IRolesViewModel
) {
    // Collect state from the ViewModel
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Fetch data when the screen is first displayed
    LaunchedEffect(Unit) {
        viewModel.fetchUserRoles()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Roles/Titles") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { /* TODO: Navigate to Add/Edit Roles Screen */ }) {
                Icon(Icons.Default.Add, contentDescription = "Add Role")
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { paddingValues ->
        when (val state = uiState) {
            is RolesUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is RolesUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            is RolesUiState.Success -> {
                if (state.roles.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No roles added yet. Tap '+' to add your first role!",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.roles) { role ->
                            RoleItemCard(role = role, onClick = {
                                // TODO: Navigate to role details/edit screen
                            })
                        }
                    }
                }
            }
        }
    }
}

// Update RoleItemCard to use your UI model
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleItemCard(role: RoleUI, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = role.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
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
fun PreviewRolesTitlesScreen() {
    MaterialTheme {
        // For a proper preview, you would create and use a FakeRolesViewModel
        RolesTitlesScreen(navController = rememberNavController(), viewModel = FakeRolesViewModel())
    }
}