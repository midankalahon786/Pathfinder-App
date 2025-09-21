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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.pathfinder.R
import com.example.pathfinder.ui.IThemeViewModel
import com.example.pathfinder.ui.screens.fake.FakeAuthViewModel
import com.example.pathfinder.ui.screens.fake.FakeThemeViewModel
import com.example.pathfinder.ui.screens.fake.FakeUserViewModel
import com.example.pathfinder.ui.theme.DarkGrayText
import com.example.pathfinder.ui.theme.DividerColor
import com.example.pathfinder.ui.theme.GraySwitchUser
import com.example.pathfinder.ui.theme.LightPurpleBackground
import com.example.pathfinder.ui.theme.MediumGrayText
import com.example.pathfinder.ui.theme.PathfinderAITheme
import com.example.pathfinder.ui.theme.RedLogOut
import com.example.pathfinder.viewmodel.IAuthViewModel
import com.example.pathfinder.viewmodel.IUserViewModel
import com.example.pathfinder.viewmodel.UserState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    authViewModel: IAuthViewModel,
    userViewModel: IUserViewModel,
    themeViewModel: IThemeViewModel
) {
    val userState by userViewModel.userState.collectAsStateWithLifecycle()
    val isDarkTheme by themeViewModel.isDarkTheme.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        userViewModel.fetchCurrentUser()
    }

    val onLogout: () -> Unit = {
        authViewModel.logout()
        navController.navigate(Screen.Login.route) {
            popUpTo(navController.graph.startDestinationId) { inclusive = true }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightPurpleBackground)
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        when (val state = userState) {
            is UserState.Loading -> {
                Box(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is UserState.Success -> {
                // Display the actual user data
                state.user?.let { user ->
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
                                text = user.name ?: "N/A",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = DarkGrayText,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                            Text(
                                text = "Email: ${user.email}",
                                style = MaterialTheme.typography.bodySmall.copy(color = MediumGrayText)
                            )
                        }
                    }
                }
            }
            is UserState.Error -> {
                // Show an error message
                Text("Error: ${state.message}", color = Color.Red, modifier = Modifier.padding(bottom = 24.dp))
            }
        }

        // Account Section
        SectionCard(
            title = "ACCOUNT",
            items = listOf(
                painterResource(R.drawable.outline_person_24) to "Personal details",
                painterResource(R.drawable.baseline_star_24) to "Skills Tab",
                painterResource(R.drawable.outline_person_book_24) to "Roles/Titles",
                painterResource(R.drawable.baseline_work_24) to "Projects"
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
                if(clickedItem == "Projects"){
                    navController.navigate(Screen.Projects.route)
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
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp, horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.baseline_dark_mode_24), // Add a dark mode icon to your drawables
                    contentDescription = "Dark Mode",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Dark Mode",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = isDarkTheme,
                    onCheckedChange = { themeViewModel.setTheme(it) }
                )
            }
        }

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
    PathfinderAITheme {
        val fakeNavController = rememberNavController()
        SettingsScreen(
            navController = fakeNavController,
            authViewModel = FakeAuthViewModel(),
            userViewModel = FakeUserViewModel(),
            themeViewModel = FakeThemeViewModel()
        )
    }
}
