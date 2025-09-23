package com.example.pathfinder.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pathfinder.graphql.GetSkillsQuery

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSkillDialog(
    allSkills: List<GetSkillsQuery.GetSkill>,
    onDismiss: () -> Unit,
    onConfirm: (skillId: String, level: String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedSkill by remember { mutableStateOf<GetSkillsQuery.GetSkill?>(null) }
    var proficiency by remember { mutableStateOf("Beginner") }
    var isSkillDropdownExpanded by remember { mutableStateOf(false) }
    var isProficiencyDropdownExpanded by remember { mutableStateOf(false) }

    val proficiencyLevels = listOf("Beginner", "Intermediate", "Advanced", "Certified")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Skill") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Searchable Skill Dropdown
                ExposedDropdownMenuBox(
                    expanded = isSkillDropdownExpanded,
                    onExpandedChange = { isSkillDropdownExpanded = !isSkillDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = {
                            searchQuery = it
                            isSkillDropdownExpanded = true
                        },
                        label = { Text("Skill Name") },
                        modifier = Modifier.menuAnchor()
                    )
                    val filteredSkills = allSkills.filter {
                        it.name.contains(searchQuery, ignoreCase = true)
                    }
                    if (filteredSkills.isNotEmpty()) {
                        ExposedDropdownMenu(
                            expanded = isSkillDropdownExpanded,
                            onDismissRequest = { isSkillDropdownExpanded = false }
                        ) {
                            filteredSkills.forEach { skill ->
                                DropdownMenuItem(
                                    text = { Text(skill.name) },
                                    onClick = {
                                        selectedSkill = skill
                                        searchQuery = skill.name
                                        isSkillDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Proficiency Dropdown
                ExposedDropdownMenuBox(
                    expanded = isProficiencyDropdownExpanded,
                    onExpandedChange = { isProficiencyDropdownExpanded = !isProficiencyDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = proficiency,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Proficiency Level") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isProficiencyDropdownExpanded) },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = isProficiencyDropdownExpanded,
                        onDismissRequest = { isProficiencyDropdownExpanded = false }
                    ) {
                        proficiencyLevels.forEach { level ->
                            DropdownMenuItem(
                                text = { Text(level) },
                                onClick = {
                                    proficiency = level
                                    isProficiencyDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    selectedSkill?.let {
                        onConfirm(it.id, proficiency)
                    }
                },
                enabled = selectedSkill != null
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}