package com.example.pathfinder.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.pathfinder.R
import com.example.pathfinder.model.UserSkillUI
import com.example.pathfinder.ui.screens.fake.FakeOnboardingViewModel
import com.example.pathfinder.ui.theme.PathfinderAITheme // Import your theme
import com.example.pathfinder.viewmodel.IOnboardingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkillsExpertiseScreen(
    navController: NavController,
    viewModel: IOnboardingViewModel
) {

    var menuExpanded by remember { mutableStateOf(false) }
    var expandedStateMap by remember { mutableStateOf<Map<String, Boolean>>(emptyMap()) }

    val userSkills by viewModel.userSkills.collectAsStateWithLifecycle()
    val allSkills by viewModel.allSkills.collectAsStateWithLifecycle()
    val searchQuery by viewModel.skillSearchQuery.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.fetchAllSkills()
    }

    val completedSteps = listOf(
        "Basic Info" to Screen.BasicInfo.route,
        "Skills & Expertise" to Screen.SkillsExpertise.route,
        "Career Goals" to Screen.CareerGoals.route
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Skills & Expertise", fontWeight = FontWeight.Bold) },
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
        },
        // THEME: Use theme background color
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Your Skills & Abilities",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                // THEME: Use onBackground color for text on the main screen
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // Search card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                // THEME: Use surface color for cards
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Select your current skills here",
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.onSkillSearchQueryChange(it) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Search for skills", style = MaterialTheme.typography.bodySmall) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
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
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Skill-set card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Skill-Set",
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    userSkills.forEachIndexed { index, skill ->
                        SkillProficiencyRow(
                            skill = skill,
                            onSkillChange = { updatedSkill ->
                                viewModel.onUserSkillChange(index, updatedSkill)
                            },
                            isExpanded = expandedStateMap[skill.userSkillId] ?: false,
                            onExpandedChange = { isExpanded ->
                                expandedStateMap = expandedStateMap.toMutableMap().apply {
                                    this[skill.userSkillId] = isExpanded
                                }
                            },
                            onRemove = { viewModel.removeUserSkill(skill.userSkillId) } // <--- Add this line
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    IconButton(
                        onClick = { viewModel.onAddNewSkillRow() },
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            // THEME: Use theme outline color for border
                            .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                    ) {
                        // THEME: Use theme color for icon tint
                        Icon(Icons.Default.Add, contentDescription = "Add Skill", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            NavigationButton(
                text = "Basic Info",
                onClick = { navController.navigate(Screen.BasicInfo.route) },
                leadingIcon = painterResource(R.drawable.baseline_arrow_back_24)
            )

            Spacer(modifier = Modifier.height(16.dp))

            NavigationButton(
                text = "Career Goals & Ambitions",
                onClick = { navController.navigate(Screen.CareerGoals.route) },
                leadingIcon = painterResource(R.drawable.baseline_rocket_launch_24),
                trailingIcon = painterResource(R.drawable.outline_chevron_right_24)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkillProficiencyRow(
    skill: UserSkillUI,
    onSkillChange: (UserSkillUI) -> Unit,
    isExpanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onRemove: () -> Unit // <--- Add this new parameter
) {
    val proficiencyOptions = listOf("Beginner", "Intermediate", "Advanced", "Certified")

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = skill.name,
            onValueChange = { onSkillChange(skill.copy(name = it)) },
            modifier = Modifier.weight(1f),
            placeholder = { Text("Enter Skill", style = MaterialTheme.typography.bodySmall) },
            shape = RoundedCornerShape(8.dp),
            textStyle = MaterialTheme.typography.bodySmall
        )

        ExposedDropdownMenuBox(
            expanded = isExpanded,
            onExpandedChange = onExpandedChange,
            modifier = Modifier.weight(0.8f)
        ) {
            OutlinedTextField(
                readOnly = true,
                value = skill.level,
                onValueChange = {},
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
                modifier = Modifier.menuAnchor(),
                shape = RoundedCornerShape(8.dp),
                textStyle = MaterialTheme.typography.bodySmall
            )
            ExposedDropdownMenu(
                expanded = isExpanded,
                onDismissRequest = { onExpandedChange(false) }
            ) {
                proficiencyOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onSkillChange(skill.copy(level = option))
                            onExpandedChange(false)
                        }
                    )
                }
            }
        }
        // <--- Add the IconButton here
        IconButton(onClick = onRemove) {
            Icon(
                Icons.Default.Delete,
                contentDescription = "Remove Skill",
                tint = MaterialTheme.colorScheme.error // Use error color for delete actions
            )
        }
    }
}

// THEME: Corrected NavigationButton to be theme-aware
@Composable
fun NavigationButton(
    text: String,
    onClick: () -> Unit,
    leadingIcon: Painter? = null,
    trailingIcon: Painter? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        // THEME: Use theme colors for border and container
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (leadingIcon != null) {
                // THEME: Use theme color for icons
                Icon(leadingIcon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text,
                modifier = Modifier.weight(1f),
                // THEME: Use theme color for text
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )
            if (trailingIcon != null) {
                Icon(trailingIcon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSkillsExpertiseScreen() {
    // THEME: Wrap your preview in your app's theme
    PathfinderAITheme {
        SkillsExpertiseScreen(navController = rememberNavController(), viewModel = FakeOnboardingViewModel())
    }
}