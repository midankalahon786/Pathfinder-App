package com.example.pathfinder.ui.screens

import android.app.DatePickerDialog
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.pathfinder.R
import com.example.pathfinder.graphql.type.Gender
import com.example.pathfinder.ui.screens.fake.FakeProfileViewModel
import com.example.pathfinder.viewmodel.IProfileViewModel
import com.example.pathfinder.viewmodel.ProfileUiState
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasicDetailsScreen(
    navController: NavController,
    viewModel: IProfileViewModel
) {
    // State collection remains the same
    val name by viewModel.name.collectAsState()
    val phone by viewModel.phone.collectAsState()
    val email by viewModel.email.collectAsState()
    val birthday by viewModel.birthday.collectAsState()
    val gender by viewModel.gender.collectAsState()
    val profileImageUrl by viewModel.profileImageUrl.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, selectedYear, selectedMonth, selectedDayOfMonth ->
            viewModel.onBirthdayChange("$selectedDayOfMonth/${selectedMonth + 1}/$selectedYear")
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    LaunchedEffect(Unit) {
        viewModel.fetchProfile()
    }

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is ProfileUiState.Success -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                viewModel.resetUiState()
            }
            is ProfileUiState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                viewModel.resetUiState()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Basic Details", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                // THEME: Remove hardcoded color to use MaterialTheme defaults
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
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
                // SIZE: Reduced horizontal padding
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // SIZE: Reduced spacer
            Spacer(modifier = Modifier.height(24.dp))

            // Profile Image Box
            Box(
                // SIZE: Reduced image size
                modifier = Modifier.size(100.dp),
                contentAlignment = Alignment.BottomEnd // A common pattern for edit icons
            ) {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = imageUri ?: profileImageUrl ?: R.drawable.outline_person_24
                    ),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        // THEME: Use theme color for placeholder background
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        // SIZE: Reduced icon box size
                        .size(32.dp)
                        .clip(CircleShape)
                        // THEME: Use primary theme color
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable { imagePickerLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.Edit,
                        contentDescription = "Edit Picture",
                        // THEME: Use onPrimary for content on primary color
                        tint = MaterialTheme.colorScheme.onPrimary,
                        // SIZE: Reduced icon size
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // SIZE: Reduced spacer
            Spacer(modifier = Modifier.height(32.dp))

            // Input Fields
            DetailTextField(
                label = "Name",
                value = name,
                onValueChange = { viewModel.onNameChange(it) }
            )
            // SIZE: Reduced spacer
            Spacer(modifier = Modifier.height(16.dp))

            GenderDropdown(
                selectedGender = gender,
                onGenderSelected = { viewModel.onGenderChange(it) }
            )
            Spacer(modifier = Modifier.height(16.dp))

            DetailTextField(
                label = "Phone",
                value = phone,
                onValueChange = { viewModel.onPhoneChange(it) }
            )
            Spacer(modifier = Modifier.height(16.dp))
            DetailTextField(
                label = "Email",
                value = email,
                onValueChange = { /* Email is read-only */ },
                readOnly = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            DetailTextField(
                label = "Birthday",
                value = birthday,
                onValueChange = { viewModel.onBirthdayChange(it) },
                placeholder = "Set birthday",
                readOnly = true,
                onClick = { datePickerDialog.show() }
            )

            Spacer(modifier = Modifier.weight(1f))

            // Buttons
            Button(
                onClick = { /* TODO: Implement Change Password */ },
                modifier = Modifier
                    .fillMaxWidth()
                    // SIZE: Reduced button height
                    .height(44.dp),
                shape = RoundedCornerShape(8.dp),
                // THEME: Use secondary container for less important actions
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Text(
                    "Change Password",
                    // THEME: Use corresponding on-color
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    // SIZE: Reduced font size
                    fontSize = 14.sp
                )
            }

            // SIZE: Reduced spacer
            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { viewModel.saveChanges() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(8.dp),
                // THEME: Use primary color for the main action
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                enabled = uiState != ProfileUiState.Loading
            ) {
                if (uiState == ProfileUiState.Loading) {
                    CircularProgressIndicator(
                        // SIZE: Reduced indicator size
                        modifier = Modifier.size(20.dp),
                        // THEME: Use on-color for contrast
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(
                        "Save changes",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            // SIZE: Reduced bottom spacer
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun DetailTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    readOnly: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            // THEME: Use theme color for labels
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold,
            // SIZE: Reduced bottom padding
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Box(modifier = Modifier.then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)) {
            TextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { if (placeholder.isNotEmpty()) Text(placeholder) },
                shape = RoundedCornerShape(8.dp),
                // THEME: Use theme-aware colors for text fields
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.outline
                ),
                singleLine = true,
                readOnly = readOnly,
                enabled = !readOnly
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenderDropdown(
    selectedGender: Gender?,
    onGenderSelected: (Gender) -> Unit
) {
    val options = Gender.entries.filter { it != Gender.UNKNOWN__ }
    var expanded by remember { mutableStateOf(false) }
    val displayText = selectedGender?.name?.replace("_", " ")?.lowercase(Locale.ROOT)
        ?.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
        ?: "Select Gender"

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Gender",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                readOnly = true,
                value = displayText,
                onValueChange = {},
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                shape = RoundedCornerShape(8.dp),
                // THEME: Use theme-aware colors for the dropdown text field
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.outline
                ),
            )
            // THEME: The dropdown menu itself is automatically themed by Material 3
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = selectionOption.name.replace("_", " ")
                                    .lowercase(Locale.ROOT)
                                    .replaceFirstChar { it.titlecase(Locale.ROOT) }
                            )
                        },
                        onClick = {
                            onGenderSelected(selectionOption)
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
fun BasicDetailsScreenPreview() {
    val fakeNavController = rememberNavController()
    // Wrap preview in your theme to see changes
    MaterialTheme {
        BasicDetailsScreen(fakeNavController, viewModel = FakeProfileViewModel())
    }
}