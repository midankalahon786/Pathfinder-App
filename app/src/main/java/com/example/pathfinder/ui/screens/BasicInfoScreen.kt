package com.example.pathfinder.ui.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.pathfinder.R
import com.example.pathfinder.ui.screens.fake.FakeOnboardingViewModel
import com.example.pathfinder.ui.theme.PathfinderAITheme // Import your theme
import com.example.pathfinder.viewmodel.IOnboardingViewModel
import com.example.pathfinder.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasicInfoScreen(
    navController: NavController,
    viewModel: IOnboardingViewModel
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val name by viewModel.name.collectAsStateWithLifecycle()
    val email by viewModel.email.collectAsStateWithLifecycle()
    val currentRole by viewModel.currentRole.collectAsStateWithLifecycle()
    val yearsExperience by viewModel.yearsExperience.collectAsStateWithLifecycle()
    val highestQualification by viewModel.highestQualification.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadInitialData()
    }
    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is UiState.Success -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
            }
            is UiState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
            }
            else -> {}
        }
    }

    val completedSteps = listOf(
        "Basic Info" to Screen.BasicInfo.route,
        "Skills & Expertise" to Screen.SkillsExpertise.route,
        "Career Goals" to Screen.CareerGoals.route
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Basic Info", fontWeight = FontWeight.Bold) },
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
                .verticalScroll(rememberScrollState())
        ) {
            // Main card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Text(
                        text = "Welcome To Your Career Path",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        // THEME: Use onSurface color for text on cards
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Let's get to know you better....",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "The following information will help us find perfect recommendations for you !!!",
                        style = MaterialTheme.typography.bodySmall,
                        // THEME: Use onSurfaceVariant for secondary text
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    // Fields
                    InfoTextField(
                        label = "Name",
                        value = name,
                        onValueChange = { viewModel.onNameChange(it) }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    InfoTextField(
                        label = "Email",
                        value = email,
                        onValueChange = { },
                        readOnly = true
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    InfoTextField(
                        label = "Current / Previous Job Title",
                        value = currentRole,
                        onValueChange = { viewModel.onCurrentRoleChange(it) }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    ExperienceDropdown(
                        selectedValue = if (yearsExperience > 0) "$yearsExperience Year(s)" else "Select Experience",
                        onSelectionChanged = {
                            val years = it.filter { char -> char.isDigit() }.toIntOrNull() ?: 0
                            viewModel.onYearsExperienceChange(years)
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    InfoTextField(
                        label = "Highest Qualification",
                        value = highestQualification,
                        onValueChange = { viewModel.onHighestQualificationChange(it) }
                    )
                    Spacer(modifier = Modifier.height(32.dp))

                    // Save button
                    Button(
                        onClick = {
                            viewModel.saveBasicInfo()
                            navController.navigate(Screen.SkillsExpertise.route)
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(8.dp),
                        // THEME: Use primary color for main action buttons
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        // THEME: Text color is automatically handled by the button
                        Text("Save & Continue", fontSize = 16.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Skills card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .padding(vertical = 16.dp, horizontal = 20.dp)
                        .clickable {
                            navController.navigate(Screen.SkillsExpertise.route) {
                                launchSingleTop = true
                            }
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = "Skills Icon",
                        // THEME: Use theme color for icons
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Skills & Expertise",
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold
                    )
                    Icon(
                        painterResource(R.drawable.outline_chevron_right_24),
                        contentDescription = "Navigate",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    readOnly: Boolean = false
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            // THEME: Use theme color for labels
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            // THEME: Use theme colors for text fields
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.outline
            ),
            singleLine = true,
            readOnly = readOnly
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExperienceDropdown(
    selectedValue: String,
    onSelectionChanged: (String) -> Unit
) {
    val options = listOf("0 Years", "1 Year", "2 Years", "3 Years", "4 Years", "5+ Years")
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Years of Experience",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                readOnly = true,
                value = selectedValue,
                onValueChange = { },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                // THEME: Use theme colors for the dropdown text field
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.outline
                ),
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text(text = selectionOption) },
                        onClick = {
                            onSelectionChanged(selectionOption)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewBasicInfoScreen() {
    val fakeNavController: NavController = rememberNavController()
    PathfinderAITheme {
        BasicInfoScreen(fakeNavController, viewModel = FakeOnboardingViewModel())
    }
}