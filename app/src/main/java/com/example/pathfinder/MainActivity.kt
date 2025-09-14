package com.example.pathfinder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.pathfinder.ui.screens.PathfinderApp
import com.example.pathfinder.ui.theme.PathfinderAITheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PathfinderAITheme {
                PathfinderApp()
            }
        }
    }
}

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    // For the screen before the main app (has no bottom nav bar)
    object Login : Screen("login", "Login", Icons.Default.Home)

    // A route to represent the entire part of the app that HAS a bottom nav bar
    object Main : Screen("main", "Main", Icons.Default.Home)

    // Screens that are destinations within the bottom nav bar
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Advisor : Screen("advisor", "Advisor", Icons.Default.Email)
    object Profile : Screen("profile", "Profile", Icons.Default.Person)
}

