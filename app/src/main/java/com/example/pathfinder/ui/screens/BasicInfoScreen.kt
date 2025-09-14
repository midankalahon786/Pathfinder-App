package com.example.pathfinder.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.pathfinder.R
import com.example.pathfinder.ui.theme.DarkGrayText
import com.example.pathfinder.ui.theme.LightGrayBackground
import com.example.pathfinder.ui.theme.MediumGrayText
import com.example.pathfinder.ui.theme.TealButton
import com.example.pathfinder.ui.theme.White

// 🎨 Color palette
val TealHeader = Color(0xFF4DB6AC)
val LightGrayField = Color(0xFFF0F0F0)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasicInfoScreen(
    navController: NavController
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Basic Info", color = White) },
                navigationIcon = {
                    IconButton(onClick = { /* Handle menu click */ }) {
                        Icon(Icons.Filled.Menu, contentDescription = "Menu", tint = White)
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
                    InfoTextField(label = "Name", initialValue = "James Harrid")
                    Spacer(modifier = Modifier.height(16.dp))
                    InfoTextField(label = "Email", initialValue = "example@email.com")
                    Spacer(modifier = Modifier.height(16.dp))
                    InfoTextField(label = "Current / Previous Job Title", initialValue = "Software Engineer")
                    Spacer(modifier = Modifier.height(16.dp))
                    ExperienceDropdown()
                    Spacer(modifier = Modifier.height(16.dp))
                    InfoTextField(label = "Highest Qualification", initialValue = "B.Tech CSE")
                    Spacer(modifier = Modifier.height(32.dp))

                    // Save button
                    Button(
                        onClick = { /* Handle Save action */ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TealButton)
                    ) {
                        Text("Save", color = White, fontSize = 16.sp)
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
fun InfoTextField(label: String, initialValue: String) {
    var text by remember { mutableStateOf(TextFieldValue(initialValue)) }
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = DarkGrayText,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        TextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = LightGrayField,
                unfocusedContainerColor = LightGrayField,
                disabledContainerColor = LightGrayField,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            singleLine = true
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExperienceDropdown() {
    val options = listOf("1 Year", "2 Years", "3 Years", "4 Years", "5+ Years")
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf(options[0]) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Years of Experience",
            color = DarkGrayText,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                readOnly = true,
                value = selectedOptionText,
                onValueChange = { },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = White,
                    unfocusedContainerColor = White,
                    disabledContainerColor = White,
                    focusedIndicatorColor = Color.Gray,
                    unfocusedIndicatorColor = Color.LightGray
                ),
                modifier = Modifier.fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text(text = selectionOption) },
                        onClick = {
                            selectedOptionText = selectionOption
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
        BasicInfoScreen(fakeNavController)
    }
}
