package com.example.pathfinder.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.pathfinder.ui.theme.PathfinderAITheme
import com.example.pathfinder.ui.theme.RedLogOut
import com.example.pathfinder.viewmodel.IAuthViewModel
import com.example.pathfinder.viewmodel.IUserViewModel
import com.example.pathfinder.viewmodel.UserState

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
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        when (val state = userState) {
            is UserState.Loading -> {
                Box(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is UserState.Success -> {
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
                                // THEME: Use theme color for placeholder
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = user.name ?: "N/A",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                            Text(
                                text = "Email: ${user.email}",
                                // THEME: Use on-surface-variant for secondary text
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }
                    }
                }
            }
            is UserState.Error -> {
                // THEME: Use theme error color
                Text("Error: ${state.message}", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(bottom = 24.dp))
            }
        }

        // Account Section
        SectionCard(
            title = "ACCOUNT"
        ) {
            SettingsItem(
                icon = painterResource(R.drawable.outline_person_24),
                text = "Personal details",
                onClick = { navController.navigate(Screen.BasicDetails.route) }
            )
            HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)

            SettingsItem(
                icon = painterResource(R.drawable.baseline_star_24),
                text = "Skills Tab",
                onClick = { navController.navigate(Screen.SkillsTab.route) }
            )
            HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)

            SettingsItem(
                icon = painterResource(R.drawable.outline_person_book_24),
                text = "Roles/Titles",
                onClick = { navController.navigate(Screen.RolesTitles.route) }
            )
            HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)

            SettingsItem(
                icon = painterResource(R.drawable.baseline_work_24),
                text = "Projects",
                onClick = { navController.navigate(Screen.Projects.route) }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Settings Section
        SectionCard(
            title = "SETTINGS"
        ) {
            // This is the new content block where you can place any items you want
            SettingsItem(
                icon = painterResource(R.drawable.outline_lock_24),
                text = "Account privacy",
                onClick = { navController.navigate(Screen.AccountPrivacy.route) }
            )
            HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)

            SettingsItem(
                icon = painterResource(R.drawable.outline_headphones_24),
                text = "Help & support",
                onClick = { navController.navigate(Screen.ContactUs.route) }
            )
            HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)

            SettingsItem(
                icon = painterResource(R.drawable.outline_help_24),
                text = "FAQs",
                onClick = { navController.navigate(Screen.Faq.route) }
            )
            HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)

            // Use your new switch item here!
            SettingsSwitchItem(
                icon = painterResource(R.drawable.baseline_dark_mode_24),
                text = "Dark Mode",
                checked = isDarkTheme,
                onCheckedChange = { themeViewModel.setTheme(it) }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { onLogout() },
                modifier = Modifier.weight(1f).height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RedLogOut),
                shape = RoundedCornerShape(8.dp)
            ) {
                // THEME: Text color will correctly be `onErrorContainer`
                Text("Log Out", fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = { onLogout() },
                modifier = Modifier.weight(1f).height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                shape = RoundedCornerShape(8.dp)
            ) {
                // THEME: Text color will correctly be `onSecondaryContainer`
                Text("Switch User", fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun SectionCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit // Accept composable content directly
) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
        modifier = Modifier.padding(bottom = 8.dp)
    )
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column {
            content() // Render the content that was passed in
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
        Icon(icon, contentDescription = text, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            // Add fontWeight here
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            ),
            modifier = Modifier.weight(1f)
        )
        Icon(
            painterResource(R.drawable.outline_chevron_right_24),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
@Composable
fun SettingsSwitchItem(
    icon: Painter,
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = icon,
            contentDescription = text,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
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