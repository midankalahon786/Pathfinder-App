package com.example.pathfinder.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.pathfinder.R
import com.example.pathfinder.ui.theme.DarkGrayText
import com.example.pathfinder.ui.theme.DividerColor
import com.example.pathfinder.ui.theme.GraySwitchUser
import com.example.pathfinder.ui.theme.LightPurpleBackground
import com.example.pathfinder.ui.theme.MediumGrayText
import com.example.pathfinder.ui.theme.RedLogOut

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    val onLogout: () -> Unit = {
        // Here you would clear any saved user data (tokens, preferences, etc.)
        // For example:
        // viewModel.clearUserSession()

        // Navigate to the login screen and clear the back stack
        navController.navigate(Screen.Login.route) {
            popUpTo(0) // This clears the entire back stack
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightPurpleBackground) // Background color is now on the Column
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.outline_person_24),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Anthony Moore",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = DarkGrayText,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Text(
                    text = "Joined Dec 28, 2020",
                    style = MaterialTheme.typography.bodySmall.copy(color = MediumGrayText)
                )
            }
            Icon(
                painterResource(R.drawable.outline_person_24),
                contentDescription = "User Icon",
                tint = MediumGrayText,
                modifier = Modifier.size(24.dp)
            )
        }

        // Account Section
        SectionCard(
            title = "ACCOUNT",
            items = listOf(
                painterResource(R.drawable.outline_person_24) to "Personal details",
                painterResource(R.drawable.baseline_star_24) to "Skills Tab",
                painterResource(R.drawable.outline_person_book_24) to "Roles/Titles"
            ),
            onItemClick = { clickedItem ->

                if (clickedItem == "Personal details") {
                    navController.navigate(Screen.BasicDetails.route)
                }
                if (clickedItem == "Skills Tab") {
                    navController.navigate(Screen.SkillsTab.route)
                }
                if (clickedItem == "Roles/Titles") {
                    navController.navigate(Screen.RolesTitles.route)
                }
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Settings Section
        SectionCard(
            title = "SETTINGS",
            items = listOf(
                painterResource(R.drawable.outline_lock_24) to "Account privacy",
                painterResource(R.drawable.outline_headphones_24) to "Help & support",
                painterResource(R.drawable.outline_help_24) to "FAQs"
            ),
            onItemClick = { clickedItem ->

                if (clickedItem == "Account privacy") {
                    navController.navigate(Screen.AccountPrivacy.route)
                }
                if (clickedItem == "Help & support") {
                    navController.navigate(Screen.ContactUs.route)
                }
                if (clickedItem == "FAQs") {
                    navController.navigate(Screen.Faq.route)
                }
            }

        )

        Spacer(modifier = Modifier.height(32.dp))

        // Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { onLogout() }, // Call the logout logic
                modifier = Modifier
                    .weight(0.45f)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RedLogOut),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Log Out", color = Color.White, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = { onLogout() }, // "Switch User" also uses the logout logic
                modifier = Modifier
                    .weight(0.45f)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GraySwitchUser),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Switch User", color = Color.White, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun SectionCard(title: String, items: List<Pair<Painter, String>>, onItemClick: (String) -> Unit) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelSmall.copy(color = MediumGrayText),
        modifier = Modifier.padding(bottom = 8.dp)
    )
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column {
            items.forEachIndexed { index, (icon, text) ->
                SettingsItem(icon, text, onClick = { onItemClick(text) })
                if (index != items.lastIndex) {
                    HorizontalDivider(thickness = 1.dp, color = DividerColor)
                }
            }
        }
    }
}

@Composable
fun SettingsItem(icon: Painter, text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = text, tint = DarkGrayText, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(color = DarkGrayText),
            modifier = Modifier.weight(1f)
        )
        Icon(painterResource(R.drawable.outline_chevron_right_24 ), contentDescription = null, tint =
            MediumGrayText)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAccountSettingsScreen() {
    MaterialTheme {
        SettingsScreen(navController = rememberNavController())
    }
}