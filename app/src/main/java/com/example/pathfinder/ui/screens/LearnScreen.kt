package com.example.pathfinder.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pathfinder.model.LearningResource

// This is the main entry point you would call from your NavHost
@Composable
fun LearnScreen() {
    // In a real app, you would get this from a ViewModel
    val learningResources = remember { mutableStateListOf<LearningResource>() }

    // Sample resources for preview/initial display
    if (learningResources.isEmpty()) {
        LaunchedEffect(Unit) {
            learningResources.addAll(
                listOf(
                    LearningResource("1", "Complete Python Bootcamp", "Udemy", "Course", "70.5 total hours", "Programming", "https://www.udemy.com/course/complete-python-bootcamp/"),
                    LearningResource("2", "React - The Complete Guide", "Udemy", "Course", "52.5 total hours", "Frontend Development", "https://www.udemy.com/course/react-the-complete-guide-incl-redux/"),
                    LearningResource("3", "Introduction to Data Science", "Coursera", "Course", "5 weeks", "Data Science", "https://www.coursera.org/learn/introduction-to-data-science"),
                    LearningResource("4", "Mastering Modern CSS", "Medium", "Article", "15 min read", "Frontend Development", "https://medium.com/css-bits/mastering-modern-css-a-guide-to-flexbox-grid-and-beyond-426b52c02094"),
                    LearningResource("5", "AWS Certified Solutions Architect", "A Cloud Guru", "Certification", "Self-paced", "Cloud Computing", "https://acloudguru.com/course/aws-certified-solutions-architect-associate"),
                    LearningResource("6", "The Clean Coder", "Robert C. Martin", "Book", "N/A", "Software Engineering", "https://www.amazon.com/Clean-Coder-Conduct-Professional-Programmers/dp/0137081073"),
                    LearningResource("7", "SQL Fundamentals", "Codecademy", "Course", "6 hours", "Database", "https://www.codecademy.com/learn/learn-sql")
                )
            )
        }
    }

    LearnScreenContent(learningResources = learningResources)
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LearnScreenContent(
    learningResources: List<LearningResource>,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>(null) } // To filter by category

    val categories = remember {
        learningResources.map { it.category }.distinct().sorted()
            .toMutableList().apply { add(0, "All") } // Add an "All" option
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            // 1. Use theme background color
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search learning resources") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            // 2. Remove hardcoded colors to use Material 3 defaults which are theme-aware
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                disabledContainerColor = MaterialTheme.colorScheme.surface,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.outline
            )
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Category Filter Chips
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            categories.forEach { category ->
                val isSelected = category == selectedCategory || (selectedCategory == null && category == "All")
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        selectedCategory = if (category == "All") null else category
                    },
                    label = { Text(category) },
                    // 3. Use semantic theme colors for FilterChip states
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = MaterialTheme.colorScheme.outline,
                        selectedBorderColor = MaterialTheme.colorScheme.primary,
                        enabled = true,
                        selected = false
                    )
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Filtered Resources Logic
        val filteredResources = remember(searchQuery, selectedCategory, learningResources) {
            learningResources.filter { resource ->
                val matchesSearch = resource.title.contains(searchQuery, ignoreCase = true) ||
                        resource.provider.contains(searchQuery, ignoreCase = true) ||
                        resource.category.contains(searchQuery, ignoreCase = true)

                val matchesCategory = selectedCategory == null || resource.category == selectedCategory
                matchesSearch && matchesCategory
            }
        }

        if (filteredResources.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "No learning resources found matching your criteria.",
                    // 4. Use theme color for placeholder text
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(filteredResources) { resource ->
                    LearningResourceCard(resource = resource) {
                        println("View resource clicked for ${resource.title}. URL: ${resource.url}")
                    }
                }
            }
        }
    }
}

@Composable
fun LearningResourceCard(resource: LearningResource, onViewClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        // 5. Use theme surface color for Card background
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = resource.title,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                // 6. Use theme color for primary text
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = resource.provider,
                style = MaterialTheme.typography.titleMedium,
                // 7. Use theme color for secondary text
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "${resource.type} • ${resource.duration}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Category: ${resource.category}",
                    style = MaterialTheme.typography.bodySmall,
                    // 8. Use a semantic accent color like 'tertiary'
                    color = MaterialTheme.colorScheme.tertiary,
                    fontWeight = FontWeight.SemiBold
                )
                Button(
                    onClick = onViewClick,
                    shape = RoundedCornerShape(8.dp),
                    // 9. Use theme's primary color for the button
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    // Text color will automatically be `onPrimary`, so no need to specify it
                    Text("View", fontSize = 14.sp)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LearnScreenContentPreview() {
    MaterialTheme {
        LearnScreenContent(
            learningResources = listOf(
                LearningResource("1", "Complete Python Bootcamp From Zero to Hero in Python", "Udemy", "Course", "70.5 total hours", "Programming", "url"),
                LearningResource("2", "React - The Complete Guide (incl Hooks, React Router, Redux)", "Udemy", "Course", "52.5 total hours", "Frontend Development", "url")
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LearningResourceCardPreview() {
    MaterialTheme {
        LearningResourceCard(
            resource = LearningResource(
                "1",
                "Advanced Kotlin Coroutines for Android Development",
                "Pluralsight",
                "Course",
                "8 hours",
                "Android Development",
                "https://www.pluralsight.com/courses/advanced-kotlin-coroutines-android-development"
            )
        ) { /* no-op */ }
    }
}