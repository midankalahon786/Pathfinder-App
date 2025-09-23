package com.example.pathfinder.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.pathfinder.R
import com.example.pathfinder.model.SkillUI
import com.example.pathfinder.ui.screens.fake.FakeHomeViewModel
import com.example.pathfinder.viewmodel.HomeUiState
import com.example.pathfinder.viewmodel.IHomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: IHomeViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.fetchTrendingSkills()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            // Use theme color for the background
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            OutlinedTextField(
                value = "",
                onValueChange = { /* Handle search input */ },
                placeholder = { Text("Search for skills or roles") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(8.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    disabledContainerColor = MaterialTheme.colorScheme.surface,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary, // More visible on focus
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.outline
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Trending Skills",
                // Use theme color for text
                style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.onBackground),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            when (val state = uiState) {
                is HomeUiState.Loading -> {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        SkillCard(skill = SkillUI("1", "Loading...", ""), modifier = Modifier.weight(1f).height(120.dp))
                        SkillCard(skill = SkillUI("2", "Loading...", ""), modifier = Modifier.weight(1f).height(120.dp))
                    }
                }
                is HomeUiState.Error -> {
                    Text(text = state.message, color = MaterialTheme.colorScheme.error)
                }
                is HomeUiState.Success -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        state.trendingSkills.take(2).forEach { skill ->
                            SkillCard(
                                skill = skill,
                                modifier = Modifier.weight(1f).height(120.dp),
                                onClick = {
                                    navController.navigate(Screen.SkillsDetails.createRoute(skill.name))
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Personalized Recommendations",
                style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.onBackground),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate(Screen.BasicInfo.route) },
                shape = RoundedCornerShape(8.dp),
                // Use theme color for Card
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_description_24),
                        contentDescription = "Form Icon",
                        // Use primary theme color for tint
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Fill a personalized form to get recommendations",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        // Use theme color for text on a surface
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        painter = painterResource(R.drawable.outline_chevron_right_24),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
        FloatingActionButton(
            onClick = { navController.navigate(Screen.Advisor.route) },
            // Use theme colors for FAB
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(painter = painterResource(R.drawable.robot_24px), "AI Career Advisor")
        }
    }
}

@Composable
fun SkillCard(skill: SkillUI, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    Box(
        modifier = modifier
            // Use a theme color for the card background
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = skill.name,
            style = MaterialTheme.typography.bodyLarge,
            // Use theme color for text on this surface
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(8.dp)
        )
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewPathfinderScreen() {
    MaterialTheme {
        HomeScreen(
            navController = rememberNavController(),
            viewModel = FakeHomeViewModel()
        )
    }
}