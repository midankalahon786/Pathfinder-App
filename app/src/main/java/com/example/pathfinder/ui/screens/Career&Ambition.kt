package com.example.pathfinder.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.pathfinder.R
import com.example.pathfinder.ui.theme.DarkGrayText
import com.example.pathfinder.ui.theme.DividerColor
import com.example.pathfinder.ui.theme.LightPurpleBackground
import com.example.pathfinder.ui.theme.MediumGrayText


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CareerGoalsScreen(navController: NavController) {
    
    var searchText by remember { mutableStateOf("") }
    val selectedRoles = remember {
        mutableStateListOf(
            "AI & Machine Learning",
            "Cloud Computing",
            "Cyber Security",
            "DevOps"
        )
    }
    var longTermGoalDescription by remember { mutableStateOf("") }

    Scaffold(
        containerColor = LightPurpleBackground,
        topBar = {
            TopAppBar(
                title = { Text("Career Goals & Ambitions", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { /* Handle menu */ }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TealHeader,
                )
            )
        }
        // Removed the bottomBar with the "Save" button
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // --- UPDATED HEADER ---
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
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
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
                        onValueChange = { searchText = it },
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
            }
            Spacer(modifier = Modifier.height(16.dp))

            // --- Selected Roles Card ---
            Card(shape = RoundedCornerShape(12.dp)) {
                Column(Modifier.padding(16.dp)) {
                    // You can add the drag handle icon here if needed
                    Text(
                        text = "Selected Roles & Domains",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        selectedRoles.forEach { role ->
                            RoleChip(
                                text = role,
                                onDelete = { selectedRoles.remove(role) }
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
                onValueChange = { longTermGoalDescription = it },
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
                onClick = { navController.navigateUp() }, // Navigates back
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
        onClick = { /* Optional: Handle click on the chip itself */ },
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
        CareerGoalsScreen(navController = rememberNavController())
    }
}