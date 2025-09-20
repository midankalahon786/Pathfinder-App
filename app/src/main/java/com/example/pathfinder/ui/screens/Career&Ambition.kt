package com.example.pathfinder.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.pathfinder.ui.screens.fake.FakeOnboardingViewModel
import com.example.pathfinder.ui.theme.DarkBlueText
import com.example.pathfinder.ui.theme.DarkGrayText
import com.example.pathfinder.ui.theme.DividerColor
import com.example.pathfinder.ui.theme.LightPurpleBackground
import com.example.pathfinder.ui.theme.MediumGrayText
import com.example.pathfinder.ui.theme.TealHeader
import com.example.pathfinder.ui.theme.White
import com.example.pathfinder.viewmodel.IOnboardingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CareerGoalsScreen(
    navController: NavController,
    viewModel: IOnboardingViewModel
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val searchText by viewModel.careerSearchQuery.collectAsStateWithLifecycle()
    val allCareerPaths by viewModel.allCareerPaths.collectAsStateWithLifecycle()
    val selectedRoles by viewModel.userCareerGoals.collectAsStateWithLifecycle()
    val longTermGoalDescription by viewModel.longTermGoal.collectAsStateWithLifecycle()

    val completedSteps = listOf(
        "Basic Info" to Screen.BasicInfo.route,
        "Skills & Expertise" to Screen.SkillsExpertise.route,
        "Career Goals" to Screen.CareerGoals.route
    )

    LaunchedEffect(Unit) {
        viewModel.fetchAllCareerPaths()
    }

    Scaffold(
        containerColor = LightPurpleBackground,
        topBar = {
            TopAppBar(
                title = { Text("Career Goals & Ambitions", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = White
                        )
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = { menuExpanded = true }) {
                            Icon(
                                Icons.Default.MoreVert,
                                contentDescription = "Show Steps",
                                tint = White
                            )
                        }
                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false }
                        ) {
                            completedSteps.forEach { (label, route) ->
                                DropdownMenuItem(
                                    text = { Text(label) },
                                    onClick = {
                                        navController.navigate(route)
                                        menuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TealHeader,
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Your Ambitions",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = DarkBlueText
                ),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text(
                text = "Tell us what you want to achieve.\nThis will help us tailor recommendations to your goals!",
                style = MaterialTheme.typography.bodyMedium,
                color = MediumGrayText,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(24.dp))

            // --- Desired Roles Card ---
            Card(shape = RoundedCornerShape(12.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        text = "Desired Role or Domains",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = searchText,
                        onValueChange = { viewModel.onCareerSearchQueryChange(it) },
                        placeholder = { Text("Search for roles") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = LightPurpleBackground,
                            disabledContainerColor = Color.White,
                            focusedIndicatorColor = DividerColor,
                            unfocusedIndicatorColor = DividerColor
                        )
                    )
                }

                val filteredPaths = allCareerPaths.filter {
                    it.title.contains(searchText, ignoreCase = true) &&
                            selectedRoles.none { sr -> sr.id == it.id }
                }
                if (searchText.isNotBlank() && filteredPaths.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier
                            .heightIn(max = 200.dp)
                            .padding(top = 8.dp)
                    ) {
                        items(filteredPaths) { careerPath ->
                            Text(
                                text = careerPath.title,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.addUserCareerGoal(careerPath.id)
                                        viewModel.onCareerSearchQueryChange("") // Clear search
                                    }
                                    .padding(vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- Selected Roles ---
            Card(shape = RoundedCornerShape(12.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        text = "Selected Roles & Domains",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        selectedRoles.forEach { role ->
                            RoleChip(
                                text = role.title,
                                onDelete = { viewModel.removeUserCareerGoal(role.id) }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- Long Term Goals Input ---
            Text(
                "Long Term Career Goals (brief description)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = longTermGoalDescription,
                onValueChange = { viewModel.onLongTermGoalChange(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,
                    focusedIndicatorColor = DividerColor,
                    unfocusedIndicatorColor = DividerColor
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- Navigation Buttons ---
            NavigationButton(
                text = "Skills & Expertise",
                onClick = { navController.navigateUp() },
                leadingIcon = painterResource(R.drawable.baseline_arrow_back_24)
            )

            Spacer(modifier = Modifier.height(16.dp))

            NavigationButton(
                text = "Past Projects & Final Review",
                onClick = { navController.navigate(Screen.FinalReview.route) },
                leadingIcon = painterResource(R.drawable.baseline_workspace_premium_24),
                trailingIcon = painterResource(R.drawable.outline_chevron_right_24)
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun RoleChip(text: String, onDelete: () -> Unit) {
    OutlinedButton(
        onClick = { /* Optional: Handle click */ },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        border = BorderStroke(1.dp, DividerColor)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                modifier = Modifier.weight(1f),
                color = DarkGrayText
            )
            IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete $text",
                    tint = MediumGrayText
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CareerGoalsScreenPreview() {
    MaterialTheme {
        CareerGoalsScreen(
            navController = rememberNavController(),
            viewModel = FakeOnboardingViewModel()
        )
    }
}
