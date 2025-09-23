package com.example.pathfinder.ui.screens


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.pathfinder.R
import com.example.pathfinder.ui.theme.PathfinderAITheme // Import your theme

data class FaqItem(val question: String, val answer: String)

val faqList = listOf(
    FaqItem("How do I update my profile?", "You can update your profile by navigating to the Settings tab, then selecting 'Personal details' to edit your information."),
    FaqItem("How does the recommendation algorithm work?", "Our algorithm analyzes your skills, stated goals, and industry trends to provide personalized career path and skill recommendations."),
    FaqItem("Can I reset my password?", "Yes. You can reset your password from the 'Personal details' screen, which has a 'Change Password' option."),
    FaqItem("Is my data secure?", "Absolutely. We prioritize user privacy and data security. You can manage your data settings in the 'Account Privacy' section.")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FaqScreen(navController: NavController) {
    var expandedIndex by remember { mutableIntStateOf(-1) }

    Scaffold(
        // THEME: Use theme background color
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("FAQs", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                // THEME: Use theme background color for a seamless look
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(faqList) { index, faqItem ->
                FaqCard(
                    faqItem = faqItem,
                    isExpanded = expandedIndex == index,
                    onClick = {
                        expandedIndex = if (expandedIndex == index) -1 else index
                    }
                )
            }
        }
    }
}

@Composable
private fun FaqCard(faqItem: FaqItem, isExpanded: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        // THEME: Use theme surface color for cards
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .clickable(onClick = onClick)
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = faqItem.question,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f),
                    // THEME: Use onSurface for primary text on cards
                    color = MaterialTheme.colorScheme.onSurface
                )
                Icon(
                    painter = if (isExpanded) painterResource(R.drawable.baseline_expand_less_24)
                    else painterResource(R.drawable.baseline_expand_more_24),
                    contentDescription = "Expand or collapse",
                    // THEME: Use onSurfaceVariant for icons
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            AnimatedVisibility(visible = isExpanded) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = faqItem.answer,
                        style = MaterialTheme.typography.bodyMedium,
                        // THEME: Use onSurfaceVariant for secondary text
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FaqScreenPreview() {
    // It's a good practice to wrap previews in your app's theme
    PathfinderAITheme {
        FaqScreen(navController = rememberNavController())
    }
}