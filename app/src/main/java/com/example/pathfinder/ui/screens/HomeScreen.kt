package com.example.pathfinder.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.pathfinder.R
import com.example.pathfinder.model.Recommendations
import com.example.pathfinder.model.SkillUI
import com.example.pathfinder.ui.screens.fake.FakeHomeViewModel
import com.example.pathfinder.ui.theme.DarkGrayText
import com.example.pathfinder.ui.theme.GrayPlaceholder
import com.example.pathfinder.ui.theme.LightPurpleBackground
import com.example.pathfinder.ui.theme.Teal500
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
            .background(LightPurpleBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Search Bar
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
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Trending Skills
            Text(
                text = "Trending Skills",
                style = MaterialTheme.typography.titleMedium.copy(color = DarkGrayText),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            when(val state = uiState) {
                is HomeUiState.Loading -> {
                    // Show placeholders or a loading indicator
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
                        // Take the first 2 skills as trending
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
                style = MaterialTheme.typography.titleMedium.copy(color = DarkGrayText),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate(Screen.BasicInfo.route) },
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_description_24),
                        contentDescription = "Form Icon",
                        tint = Teal500
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Fill a personalized form to get recommendations",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = DarkGrayText,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        painter = painterResource(R.drawable.outline_chevron_right_24),
                        contentDescription = null
                    )
                }
            }
            // --- END of added card ---

            Spacer(modifier = Modifier.height(16.dp))

        }

        FloatingActionButton(
            onClick = { navController.navigate(Screen.Advisor.route) },
            containerColor = Teal500,
            contentColor = Color.White,
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
            .background(GrayPlaceholder, RoundedCornerShape(8.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = skill.name,
            style = MaterialTheme.typography.bodyLarge,
            color = DarkGrayText,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Composable
fun RecommendationCard(recommendation: Recommendations, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(GrayPlaceholder, RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = recommendation.name,
            style = MaterialTheme.typography.bodyLarge,
            color = DarkGrayText,
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