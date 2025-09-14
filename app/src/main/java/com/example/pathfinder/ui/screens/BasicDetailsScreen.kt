package com.example.pathfinder.ui.screens

import android.app.DatePickerDialog
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.pathfinder.R
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasicDetailsScreen(
    navController: NavController
) {
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var birthdayText by remember { mutableStateOf("") }
    val context = LocalContext.current
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val datePickerDialog = DatePickerDialog(
        context,
        { _, selectedYear, selectedMonth, selectedDayOfMonth ->
            birthdayText = "$selectedDayOfMonth/${selectedMonth + 1}/$selectedYear"
        }, year, month, day
    )

    Scaffold(
        topBar = {
            // CHANGED: Using CenterAlignedTopAppBar for better title centering
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Basic Details",
                        // No need for manual alignment modifiers here
                        fontWeight = FontWeight.Bold
                    )
                },
                // ADDED: The back button is the navigationIcon
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Profile Image Box
            Box(
                modifier = Modifier.size(120.dp),
                contentAlignment = Alignment.TopEnd
            ) {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = imageUri ?: R.drawable.outline_person_24
                    ),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(Color.LightGray),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF16DBC4))
                        .clickable { imagePickerLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.Edit,
                        contentDescription = "Edit Picture",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Input Fields
            DetailTextField(label = "Name", initialValue = "James Harrid")
            Spacer(modifier = Modifier.height(24.dp))
            DetailTextField(label = "Phone", initialValue = "123-456-7890")
            Spacer(modifier = Modifier.height(24.dp))
            DetailTextField(label = "Email", initialValue = "example@email.com")
            Spacer(modifier = Modifier.height(24.dp))
            DetailTextField(
                label = "Birthday",
                initialValue = birthdayText,
                placeholder = "Set birthday",
                readOnly = true,
                onClick = { datePickerDialog.show() }
            )

            Spacer(modifier = Modifier.weight(1f))

            // Buttons
            Button(
                onClick = { /* Change Password */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8D94A0))
            ) {
                Text("Change Password", color = Color.White, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { /* Save */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1DE9B6))
            ) {
                Text("Save changes", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// DetailTextField and Preview composables remain the same...

@Composable
fun DetailTextField(
    label: String,
    initialValue: String = "",
    placeholder: String = "",
    readOnly: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    var text by remember { mutableStateOf(TextFieldValue(initialValue)) }

    LaunchedEffect(initialValue) {
        if (text.text != initialValue) {
            text = text.copy(text = initialValue)
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = Color.DarkGray,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Box(modifier = Modifier.then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)) {
            TextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { if (placeholder.isNotEmpty()) Text(placeholder) },
                shape = RoundedCornerShape(8.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF5F5F5),
                    unfocusedContainerColor = Color(0xFFF5F5F5),
                    disabledContainerColor = Color(0xFFF5F5F5),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                singleLine = true,
                readOnly = readOnly,
                enabled = !readOnly
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BasicDetailsScreenPreview(){
    BasicDetailsScreen(navController = rememberNavController())
}