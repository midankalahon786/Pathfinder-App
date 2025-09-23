package com.example.pathfinder.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.pathfinder.ui.screens.fake.FakeOnboardingViewModel
import com.example.pathfinder.ui.theme.PathfinderAITheme // Import your theme
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
        // THEME: Use theme background color
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Career Goals & Ambitions") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = { menuExpanded = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Show Steps")
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
                // THEME: Use theme colors for the app bar
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
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
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                // THEME: Use theme color for text on background
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text(
                text = "Tell us what you want to achieve.\nThis will help us tailor recommendations to your goals!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(24.dp))

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        text = "Desired Role or Domains",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
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
                        // THEME: Use theme colors for text field
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.outline
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
                            .padding(horizontal = 16.dp)
                    ) {
                        items(filteredPaths) { careerPath ->
                            Text(
                                text = careerPath.title,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.addUserCareerGoal(careerPath.id)
                                        viewModel.onCareerSearchQueryChange("")
                                    }
                                    .padding(vertical = 12.dp),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        text = "Selected Roles & Domains",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
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

            Text(
                "Long Term Career Goals (brief description)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
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
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    disabledContainerColor = MaterialTheme.colorScheme.surface,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.outline
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

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
        // THEME: Use theme outline color for the border
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                modifier = Modifier.weight(1f),
                // THEME: Use onSurface color for the text
                color = MaterialTheme.colorScheme.onSurface
            )
            IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete $text",
                    // THEME: Use onSurfaceVariant for the icon
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CareerGoalsScreenPreview() {
    PathfinderAITheme {
        CareerGoalsScreen(
            navController = rememberNavController(),
            viewModel = FakeOnboardingViewModel()
        )
    }
}