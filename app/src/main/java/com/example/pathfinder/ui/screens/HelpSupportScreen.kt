package com.example.pathfinder.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.pathfinder.R
import com.example.pathfinder.ui.theme.PathfinderAITheme // Import your theme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpSupportScreen(navController: NavController) {
    Scaffold(
        // THEME: Use theme background color
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Help & Support", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                // THEME: Use theme background for a seamless look
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            HelpItem(
                icon = painterResource(R.drawable.baseline_contact_support_24),
                text = "Contact Us",
                onClick = { /* TODO: Navigate to Contact screen */ }
            )
            // THEME: Use a theme-aware color for the divider
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            HelpItem(
                icon = painterResource(R.drawable.baseline_report_24),
                text = "Report a Problem",
                onClick = { /* TODO: Navigate to Report screen */ }
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            HelpItem(
                icon = painterResource(R.drawable.baseline_description_24),
                text = "Terms of Service",
                onClick = { /* TODO: Navigate to Terms screen */ }
            )
        }
    }
}

@Composable
private fun HelpItem(icon: Painter, text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // THEME: Use onSurfaceVariant for icons
        Icon(painter = icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.width(16.dp))
        // THEME: Use onSurface for primary text
        Text(text, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
        // THEME: Use onSurfaceVariant for the chevron icon
        Icon(
            painter = painterResource(R.drawable.outline_chevron_right_24),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HelpSupportScreenPreview() {
    // It's a good practice to wrap previews in your app's theme
    PathfinderAITheme {
        HelpSupportScreen(navController = rememberNavController())
    }
}