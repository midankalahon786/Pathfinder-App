package com.example.pathfinder.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pathfinder.model.Job

// This is the main entry point you would call from your NavHost
@Composable
fun JobsScreen() {
    // In a real app, you would get this from a ViewModel
    val jobs = remember { mutableStateListOf<Job>() }

    // Sample jobs for preview/initial display
    if (jobs.isEmpty()) {
        LaunchedEffect(Unit) {
            jobs.addAll(
                listOf(
                    Job("1", "Software Engineer", "Tech Solutions Inc.", "New York, USA", "Full-time", "Mid-level", "$100k - $150k", "Develop and maintain software applications."),
                    Job("2", "Data Scientist", "Data Insights Corp.", "San Francisco, USA", "Full-time", "Senior", "$120k - $180k", "Analyze complex data and build predictive models."),
                    Job("3", "UX Designer", "Creative Studio", "London, UK", "Contract", "Entry-level", "$50k - $80k", "Design intuitive user experiences.")
                )
            )
        }
    }

    JobsScreenContent(jobs = jobs)
}


@Composable
fun JobsScreenContent(
    jobs: List<Job>,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            // 1. Use theme background color
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search Job Title, Company, or Location") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            // 2. Use theme-aware colors for TextField.
            //    It's often best to remove the `colors` parameter entirely
            //    to use the default Material 3 styled TextField.
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                disabledContainerColor = MaterialTheme.colorScheme.surface,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.outline
            )
        )
        Spacer(modifier = Modifier.height(16.dp))

        val filteredJobs = jobs.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
                    it.company.contains(searchQuery, ignoreCase = true) ||
                    it.location.contains(searchQuery, ignoreCase = true)
        }

        if (filteredJobs.isEmpty() && searchQuery.isNotBlank()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                // 3. Use theme color for placeholder text
                Text("No jobs found matching your search.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else if (jobs.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Loading jobs...", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(filteredJobs) { job ->
                    JobCard(job = job) {
                        println("Apply clicked for ${job.title}")
                    }
                }
            }
        }
    }
}


@Composable
fun JobCard(job: Job, onApplyClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        // 4. Use theme surface color for Card background
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = job.title,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                // 5. Use theme color for primary text on a surface
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = job.company,
                style = MaterialTheme.typography.titleMedium,
                // 6. Use theme color for secondary text
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = job.location,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${job.type} • ${job.experienceLevel}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = job.salary,
                        style = MaterialTheme.typography.bodySmall,
                        // 7. Use a semantic accent color like 'tertiary'
                        color = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Button(
                    onClick = onApplyClick,
                    shape = RoundedCornerShape(8.dp),
                    // 8. Use theme's primary color for the button
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    // Text color will automatically be `onPrimary`
                    Text("Apply", fontSize = 14.sp)
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobFilterBottomSheet(
    onApplyFilters: (jobType: String?, expLevel: String?, salaryRange: String?) -> Unit,
    onClearFilters: () -> Unit
) {
    // State variables remain the same
    var selectedJobType by remember { mutableStateOf<String?>(null) }
    var selectedExperienceLevel by remember { mutableStateOf<String?>(null) }
    var selectedSalaryRange by remember { mutableStateOf<String?>(null) }
    val jobTypes = listOf("Full-time", "Part-time", "Contract", "Internship", "Temporary")
    val experienceLevels = listOf("Entry-level", "Mid-level", "Senior", "Director", "Executive")
    val salaryRanges = listOf(
        "Less than $50k", "$50k - $80k", "$80k - $120k", "$120k - $180k", "More than $180k"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            // The bottom sheet itself provides the background color, so none needed here.
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Filter Jobs",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        FilterSection(title = "Job Type", options = jobTypes, selectedOption = selectedJobType) {
            selectedJobType = it
        }
        FilterSection(title = "Experience Level", options = experienceLevels, selectedOption = selectedExperienceLevel) {
            selectedExperienceLevel = it
        }
        FilterSection(title = "Salary Range", options = salaryRanges, selectedOption = selectedSalaryRange) {
            selectedSalaryRange = it
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = {
                    selectedJobType = null
                    selectedExperienceLevel = null
                    selectedSalaryRange = null
                    onClearFilters()
                },
                modifier = Modifier.weight(1f),
                // 9. Use theme outline color for border
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                shape = RoundedCornerShape(8.dp)
            ) {
                // Text color defaults correctly
                Text("Clear Filters")
            }
            Button(
                onClick = {
                    onApplyFilters(selectedJobType, selectedExperienceLevel, selectedSalaryRange)
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Apply Filters")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterSection(
    title: String,
    options: List<String>,
    selectedOption: String?,
    onOptionSelected: (String?) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            options.forEach { option ->
                val isSelected = option == selectedOption
                FilterChip(
                    selected = isSelected,
                    onClick = { onOptionSelected(if (isSelected) null else option) },
                    label = { Text(option) },
                    // 10. Use semantic theme colors for FilterChip states
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
    }
}


@Preview(showBackground = true)
@Composable
fun JobsScreenContentPreview() {
    MaterialTheme {
        JobsScreenContent(
            jobs = listOf(
                Job("1", "Software Engineer", "Tech Solutions Inc.", "New York, USA", "Full-time", "Mid-level", "$100k - $150k", "Develop and maintain software applications."),
                Job("2", "Data Scientist", "Data Insights Corp.", "San Francisco, USA", "Full-time", "Senior", "$120k - $180k", "Analyze complex data and build predictive models.")
            )
        )
    }
}