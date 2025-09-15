package com.example.pathfinder.ui.screens

import android.R.attr.type
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pathfinder.ui.theme.PathfinderAITheme


val bottomNavItems = listOf(Screen.Home, Screen.Jobs, Screen.Learn, Screen.Settings)

@Composable
fun PathfinderApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) {
            LoginScreen(onLoginSuccess = {
                navController.navigate(Screen.Main.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
            })
        }
        composable(Screen.Main.route) { MainScreen(navController) }
        composable(Screen.BasicInfo.route) { BasicInfoScreen(navController) }
        composable(Screen.BasicDetails.route){BasicDetailsScreen(navController)  }
        composable(Screen.SkillsExpertise.route) { SkillsExpertiseScreen(navController) }
        composable(
            route = Screen.SkillsDetails.route, // Use the route from the Screen sealed class
            arguments = listOf(navArgument("skillName") { type = NavType.StringType })
        ) { backStackEntry ->
            val skillName = backStackEntry.arguments?.getString("skillName")
            if (skillName != null) {
                SkillDetailScreen(navController = navController, skillName = skillName)
            }
        }
        composable(Screen.CareerGoals.route) { CareerGoalsScreen(navController) }
        composable(Screen.FinalReview.route) { FinalReviewScreen(navController) }
        composable(Screen.Profile.route) { ProfileScreen() }
        composable(Screen.Advisor.route) { AdvisorScreen() }
        composable(Screen.AccountPrivacy.route) { AccountPrivacyScreen(navController) }
        composable(Screen.Faq.route) { FaqScreen(navController) }
        composable(Screen.ContactUs.route) { HelpSupportScreen(navController) }


    }
}

@Composable
fun MainScreen(navController: NavController) {
    val bottomNavNavController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                val navBackStackEntry by bottomNavNavController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                bottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            bottomNavNavController.navigate(screen.route) {
                                popUpTo(bottomNavNavController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            when (val icon = screen.icon) {
                                is IconSource.Vector -> {
                                    Icon(
                                        imageVector = icon.imageVector,
                                        contentDescription = screen.label
                                    )
                                }
                                is IconSource.Resource -> {
                                    Icon(
                                        painter = painterResource(id = icon.id),
                                        contentDescription = screen.label
                                    )
                                }
                            }
                        },
                        label = { Text(screen.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = bottomNavNavController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) { HomeScreen(navController) }
            composable(Screen.Jobs.route) { JobsScreen() }
            composable(Screen.Learn.route) { LearnScreen() }
            composable(Screen.Settings.route) {
                SettingsScreen(navController = navController)
            }
        }
    }
}


@Composable
fun JobsScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Jobs Screen")
    }
}

@Composable
fun LearnScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Learn Screen")
    }
}


@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    PathfinderAITheme {
        MainScreen(navController = rememberNavController())
    }
}