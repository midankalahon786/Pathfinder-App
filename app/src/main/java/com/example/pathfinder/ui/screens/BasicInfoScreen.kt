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
import androidx.compose.ui.graphics.Color
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
import com.example.pathfinder.ui.theme.DarkGrayText
import com.example.pathfinder.ui.theme.LightGrayBackground
import com.example.pathfinder.ui.theme.LightGrayField
import com.example.pathfinder.ui.theme.MediumGrayText
import com.example.pathfinder.ui.theme.TealButton
import com.example.pathfinder.ui.theme.TealHeader
import com.example.pathfinder.ui.theme.White
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
                // You can add logic to reset the state if needed
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
                title = { Text("Basic Info", color = White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = White)
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = { menuExpanded = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Show Steps", tint = White)
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
                    containerColor = TealHeader
                )
            )
        },
        containerColor = LightGrayBackground
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
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Text(
                        text = "Welcome To Your Career Path",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = DarkGrayText
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Let's get to know you better....",
                        style = MaterialTheme.typography.titleMedium.copy(color = DarkGrayText)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "The following information will help us find perfect recommendations for you !!!",
                        style = MaterialTheme.typography.bodySmall.copy(color = MediumGrayText)
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
                        onValueChange = { /* Email is read-only from login */ },
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
                        selectedValue = "$yearsExperience Year(s)",
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
                            // Navigate to the next screen in the flow
                            navController.navigate(Screen.SkillsExpertise.route)
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TealButton)
                    ) {
                        Text("Save & Continue", color = White, fontSize = 16.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Skills card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { /* Navigate to Skills & Expertise */ },
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(vertical = 16.dp, horizontal = 20.dp)
                        .clickable{
                            navController.navigate(Screen.SkillsExpertise.route) {
                                launchSingleTop = true
                            }
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = "Skills Icon",
                        tint = MediumGrayText
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Skills & Expertise",
                        modifier = Modifier.weight(1f),
                        color = DarkGrayText,
                        fontWeight = FontWeight.SemiBold
                    )
                    Icon(
                        painterResource(R.drawable.outline_chevron_right_24),
                        contentDescription = "Navigate",
                        tint = MediumGrayText
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
            color = DarkGrayText,
            fontWeight = FontWeight.SemiBold,
            // --- CHANGED to bodySmall for a smaller label ---
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = LightGrayField,
                unfocusedContainerColor = LightGrayField,
                disabledContainerColor = LightGrayField,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
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
            color = DarkGrayText,
            fontWeight = FontWeight.SemiBold,
            // --- CHANGED to bodySmall for a smaller label ---
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                readOnly = true,
                value = selectedValue, // Use the value passed from the parent
                onValueChange = { },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = White,
                    unfocusedContainerColor = White,
                    disabledContainerColor = White,
                    focusedIndicatorColor = Color.Gray,
                    unfocusedIndicatorColor = Color.LightGray
                ),
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth() // Added menuAnchor
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text(text = selectionOption) },
                        onClick = {
                            onSelectionChanged(selectionOption) // Report the change up to the parent
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
    MaterialTheme {
        BasicInfoScreen(fakeNavController, viewModel = FakeOnboardingViewModel())
    }
}

