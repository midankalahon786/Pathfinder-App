package com.example.pathfinder.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.pathfinder.model.Recommendations
import com.example.pathfinder.model.Skill
import com.example.pathfinder.ui.theme.DarkGrayText
import com.example.pathfinder.ui.theme.GrayPlaceholder
import com.example.pathfinder.ui.theme.LightPurpleBackground
import com.example.pathfinder.ui.theme.Teal500
import com.example.pathfinder.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController
) {

    val trendingSkills by remember {
        mutableStateOf(
            listOf(
                Skill("Jetpack Compose", "Beginner"),
                Skill("Kotlin Multiplatform", "Intermediate")
            )
        )
    }

    val personalizedRecommendations by remember {
        mutableStateOf(
            listOf(
                Recommendations("Software Engineer"),
                Recommendations("UI/UX Principles")
            )
        )
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                trendingSkills.forEach { skill ->
                    SkillCard(
                        skill = skill,
                        modifier = Modifier
                            .weight(1f)
                            .height(120.dp),
                        onClick = {
                            // Navigate to the detail screen, passing the skill name as an argument
                            navController.navigate(Screen.SkillsDetails.createRoute(skill.name))
                        }
                    )
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

            // Existing Recommendations
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                personalizedRecommendations.forEach { recommendation ->
                    RecommendationCard(
                        recommendation = recommendation,
                        modifier = Modifier
                            .weight(1f)
                            .height(120.dp)
                    )
                }
            }
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
fun SkillCard(skill: Skill, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    Box(
        modifier = modifier
            .background(GrayPlaceholder, RoundedCornerShape(8.dp))
            .clickable{onClick()},
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
        HomeScreen(navController = rememberNavController())
    }
}