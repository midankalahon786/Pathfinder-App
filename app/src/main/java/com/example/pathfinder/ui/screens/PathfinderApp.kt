package com.example.pathfinder.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pathfinder.ui.ThemeViewModel
import com.example.pathfinder.ui.theme.PathfinderAITheme
import com.example.pathfinder.viewmodel.AuthViewModel
import com.example.pathfinder.viewmodel.HomeViewModel
import com.example.pathfinder.viewmodel.OnboardingViewModel
import com.example.pathfinder.viewmodel.ProfileViewModel
import com.example.pathfinder.viewmodel.ProjectsViewModel
import com.example.pathfinder.viewmodel.RolesViewModel
import com.example.pathfinder.viewmodel.SkillDetailViewModel
import com.example.pathfinder.viewmodel.SkillsViewModel
import com.example.pathfinder.viewmodel.UserViewModel


val bottomNavItems = listOf(Screen.Home, Screen.Jobs, Screen.Learn, Screen.Settings)

@Composable
fun PathfinderApp() {
    val themeViewModel: ThemeViewModel = viewModel()
    val isDarkTheme by themeViewModel.isDarkTheme.collectAsStateWithLifecycle()


    PathfinderAITheme (darkTheme = isDarkTheme){
        val navController = rememberNavController()
        val authViewModel: AuthViewModel = viewModel()
        val profileViewModel: ProfileViewModel = viewModel()
        val onboardingViewModel: OnboardingViewModel = viewModel()
        val projectViewModel: ProjectsViewModel = viewModel()
        val skillsViewModel: SkillsViewModel = viewModel()
        val rolesViewModel: RolesViewModel = viewModel()
        val skillDetailViewModel: SkillDetailViewModel = viewModel()
        NavHost(navController = navController, startDestination = Screen.Login.route) {
            composable(Screen.Login.route) {
                LoginScreen(
                    navController = navController,
                    authViewModel = authViewModel, // <-- Pass the ViewModel here
                    onLoginSuccess = {
                        navController.navigate(Screen.Main.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.Main.route) { MainScreen(navController) }
            composable(Screen.SignUp.route) {
                SignUpScreen(
                    navController = navController,
                    authViewModel = authViewModel,
                    onSignUpSuccess = {
                        navController.navigate(Screen.Main.route) {
                            // This popUpTo logic ensures the auth flow (Login/Sign Up) is cleared from the back stack
                            popUpTo(Screen.Login.route) {
                                inclusive = true
                            }
                        }
                    }
                )
            }
            composable(Screen.BasicInfo.route) {
                BasicInfoScreen(
                    navController,
                    onboardingViewModel
                )
            }
            composable(Screen.BasicDetails.route) {
                BasicDetailsScreen(
                    navController,
                    profileViewModel
                )
            }
            composable(Screen.SkillsExpertise.route) {
                SkillsExpertiseScreen(
                    navController,
                    onboardingViewModel
                )
            }
            composable(
                route = Screen.SkillsDetails.route, // Use the route from the Screen sealed class
                arguments = listOf(navArgument("skillName") { type = NavType.StringType })
            ) { backStackEntry ->
                val skillName = backStackEntry.arguments?.getString("skillName")
                if (skillName != null) {
                    SkillDetailScreen(
                        navController = navController,
                        skillName = skillName,
                        viewModel = skillDetailViewModel
                    )
                }
            }
            composable(Screen.SkillsTab.route) {
                SkillsTabScreen(
                    navController = navController,
                    skillsViewModel
                )
            }
            composable(Screen.Projects.route) { ProjectsScreen(navController, projectViewModel) }
            composable(Screen.RolesTitles.route) {
                RolesTitlesScreen(
                    navController = navController,
                    rolesViewModel
                )
            }
            composable(Screen.CareerGoals.route) {
                CareerGoalsScreen(
                    navController,
                    onboardingViewModel
                )
            }
            composable(Screen.FinalReview.route) {
                FinalReviewScreen(
                    navController,
                    onboardingViewModel
                )
            }
            composable(Screen.Profile.route) { ProfileScreen() }
            composable(Screen.Advisor.route) {
                AdvisorScreen(onNavigateUp = { navController.navigateUp() })
            }
            composable(Screen.AccountPrivacy.route) { AccountPrivacyScreen(navController) }
            composable(Screen.Faq.route) { FaqScreen(navController) }
            composable(Screen.ContactUs.route) { HelpSupportScreen(navController) }
            composable(Screen.SubmissionWelcome.route) { SubmissionWelcomeScreen(navController)}

        }
    }
}

@Composable
fun MainScreen(navController: NavController) {
    val bottomNavNavController = rememberNavController()
    val homeViewModel: HomeViewModel = viewModel()
    val themeViewModel: ThemeViewModel = viewModel()

    Scaffold(
        bottomBar = {
            // Remove the hardcoded containerColor
            NavigationBar {
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
            composable(Screen.Home.route) { HomeScreen(navController, homeViewModel) }
            composable(Screen.Jobs.route) { JobsScreen() }
            composable(Screen.Learn.route) { LearnScreen() }
            composable(Screen.Settings.route) {
                val authViewModel: AuthViewModel = viewModel()
                val userViewModel: UserViewModel = viewModel()

                SettingsScreen(
                    navController = navController,
                    authViewModel = authViewModel,
                    userViewModel = userViewModel,
                    themeViewModel = themeViewModel
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    PathfinderAITheme {
        MainScreen(navController = rememberNavController())
    }
}