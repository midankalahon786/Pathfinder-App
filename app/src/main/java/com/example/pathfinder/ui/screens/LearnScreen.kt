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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pathfinder.model.LearningResource
import com.example.pathfinder.ui.theme.*

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

    // Now, LearnScreen just calls the content composable
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
            .background(LightPurpleBackground)
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
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.White,
                focusedIndicatorColor = TealHeader,
                unfocusedIndicatorColor = DividerColor
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
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = TealHeader.copy(alpha = 0.2f),
                        selectedLabelColor = DarkBlueText,
                        containerColor = Color.White,
                        labelColor = MediumGrayText
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        selectedBorderColor = TealHeader,
                        borderColor = DividerColor,
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
                    color = MediumGrayText,
                    textAlign = TextAlign.Center
                )

            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(filteredResources) { resource ->
                    LearningResourceCard(resource = resource) {
                        // In a real app, this would open the URL, e.g., using an Intent
                        println("View resource clicked for ${resource.title}. URL: ${resource.url}")
                        // Example: val intent = Intent(Intent.ACTION_VIEW, Uri.parse(resource.url))
                        // context.startActivity(intent)
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
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = resource.title,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = DarkBlueText,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = resource.provider,
                style = MaterialTheme.typography.titleMedium,
                color = DarkGrayText
            )
            Text(
                text = "${resource.type} • ${resource.duration}",
                style = MaterialTheme.typography.bodyMedium,
                color = MediumGrayText
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
                    color = Color.Green,
                    fontWeight = FontWeight.SemiBold
                )
                Button(
                    onClick = onViewClick,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TealHeader)
                ) {
                    Text("View", color = Color.White, fontSize = 14.sp)
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