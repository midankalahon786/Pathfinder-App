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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pathfinder.model.Job
import com.example.pathfinder.ui.theme.*

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

    // Now, JobsScreen just calls the content composable
    JobsScreenContent(jobs = jobs)
}


// This composable contains the actual UI, without the Scaffold
@Composable
fun JobsScreenContent(
    jobs: List<Job>,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LightPurpleBackground)
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
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.White,
                focusedIndicatorColor = TealHeader,
                unfocusedIndicatorColor = DividerColor
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
                Text("No jobs found matching your search.", color = MediumGrayText)
            }
        } else if (jobs.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Loading jobs...", color = MediumGrayText)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(filteredJobs) { job ->
                    JobCard(job = job) {
                        // In a real app, this would trigger a ViewModel function
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
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = job.title,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = DarkBlueText
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = job.company,
                style = MaterialTheme.typography.titleMedium,
                color = DarkGrayText
            )
            Text(
                text = job.location,
                style = MaterialTheme.typography.bodyMedium,
                color = MediumGrayText
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
                        color = MediumGrayText
                    )
                    Text(
                        text = job.salary,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Green,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Button(
                    onClick = onApplyClick,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TealHeader)
                ) {
                    Text("Apply", color = Color.White, fontSize = 14.sp)
                }
            }
        }
    }
}

// NOTE: JobFilterBottomSheet and FilterSection would be used with a ModalBottomSheet
// which is typically part of a Scaffold. You would call them from your main screen
// that contains the Scaffold. They are included here for completeness.

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobFilterBottomSheet(
    onApplyFilters: (jobType: String?, expLevel: String?, salaryRange: String?) -> Unit,
    onClearFilters: () -> Unit
) {
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
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Filter Jobs",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = DarkBlueText
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
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MediumGrayText),
                border = BorderStroke(1.dp, DividerColor),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Clear Filters")
            }
            Button(
                onClick = {
                    onApplyFilters(selectedJobType, selectedExperienceLevel, selectedSalaryRange)
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = TealHeader),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Apply Filters", color = Color.White)
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
            color = DarkBlueText
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