// In your Screen.kt file
package com.example.pathfinder.ui.screens

import androidx.annotation.DrawableRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.pathfinder.R

sealed class IconSource {
    data class Vector(val imageVector: ImageVector) : IconSource()
    data class Resource(@DrawableRes val id: Int) : IconSource()
}

sealed class Screen(val route: String, val label: String, val icon: IconSource) {
    object Home : Screen("home", "Home", IconSource.Resource(R.drawable.baseline_home_24))
    object Jobs : Screen("jobs", "Jobs", IconSource.Resource(R.drawable.baseline_work_24))
    object Learn : Screen("learn", "Learn", IconSource.Resource(R.drawable.rounded_menu_book_24))
    object Settings : Screen("settings", "Settings", IconSource.Vector(Icons.Default.Settings))

    object Login : Screen("login", "Login", IconSource.Vector(Icons.Default.Person))
    object SignUp : Screen("signup", "Sign Up", IconSource.Vector(Icons.Default.Person))
    object Main : Screen("main", "Main", IconSource.Vector(Icons.Default.Person))
    object BasicInfo : Screen("basic_info", "Basic Info", IconSource.Vector(Icons.Default.Person))
    object BasicDetails: Screen("basic_details", "Basic Details", IconSource.Vector(Icons.Default.Person))
    object SkillsExpertise : Screen("skills_expertise", "Skills", IconSource.Vector(Icons.Default.Person))
    object SkillsDetails : Screen("skills_details/{skillName}", "Skills Details", IconSource.Vector(Icons.Default.Person)) {
        fun createRoute(skillName: String) = "skills_details/$skillName"
    }
    object SkillsTab : Screen("skills_tab", "Skills Tab", IconSource.Vector(Icons.Filled.Star)) // Example icon
    object Projects : Screen("projects", "Projects", IconSource.Vector(Icons.Default.Person))
    object RolesTitles : Screen("roles_titles", "Roles/Titles", IconSource.Resource(R.drawable.baseline_work_24)) // Example icon
    object CareerGoals : Screen("career_goals", "Career Goals", IconSource.Vector(Icons.Default.Person))
    object FinalReview : Screen("final_review", "Final Review", IconSource.Vector(Icons.Default.Person))
    object Profile : Screen("profile", "Profile", IconSource.Vector(Icons.Default.Person))
    object Advisor : Screen("advisor", "Advisor", IconSource.Vector(Icons.Default.Person))
    object AccountPrivacy : Screen("account_privacy", "Account Privacy", IconSource.Vector(Icons.Outlined.Lock))
    object Faq : Screen("faq", "FAQs", IconSource.Resource(R.drawable.outline_headphones_24))
    object ContactUs : Screen("contact_us", "Contact Us", IconSource.Vector(Icons.Outlined.Email))
}