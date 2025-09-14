package com.example.pathfinder.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.pathfinder.R
import com.example.pathfinder.model.Skill
import com.example.pathfinder.ui.theme.DividerColor
import com.example.pathfinder.ui.theme.LightGrayBackground
import com.example.pathfinder.ui.theme.MediumGrayText

val DarkBlueText = Color(0xFF004D40)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkillsExpertiseScreen(navController: NavController) {
    val skillsList = remember {
        // Updated to include the isSelected state
        mutableStateListOf(
            Skill("", "Proficiency", isSelected = false),
            Skill("", "Proficiency", isSelected = false),
            Skill("", "Proficiency", isSelected = false)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Skills & Expertise", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { /* Handle menu click */ }) {
                        Icon(Icons.Filled.Menu, contentDescription = "Menu", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = TealHeader)
            )
        },
        containerColor = LightGrayBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Your Skills & Abilities",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = DarkBlueText
                ),
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // Search card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Select your current skills here",
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = "",
                        onValueChange = { /* Handle search */ },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Search for skills") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                        shape = RoundedCornerShape(8.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            disabledContainerColor = Color.White,
                            focusedIndicatorColor = DividerColor,
                            unfocusedIndicatorColor = DividerColor
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Skill-set card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Skill-Set",
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    skillsList.forEachIndexed { index, skill ->
                        SkillProficiencyRow(
                            skill = skill,
                            onSkillChange = { updatedSkill -> skillsList[index] = updatedSkill }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    IconButton(
                        onClick = { skillsList.add(Skill("", "Proficiency")) },
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .border(1.dp, DividerColor, CircleShape)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Skill", tint = MediumGrayText)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            NavigationButton(
                text = "Basic Info",
                onClick = { navController.navigate(Screen.BasicInfo.route) },
                leadingIcon = painterResource(R.drawable.baseline_arrow_back_24)
            )

            Spacer(modifier = Modifier.height(16.dp))

            NavigationButton(
                text = "Career Goals & Ambitions",
                onClick = { navController.navigate(Screen.CareerGoals.route) },
                leadingIcon = painterResource(R.drawable.baseline_rocket_launch_24),
                trailingIcon = painterResource(R.drawable.outline_chevron_right_24)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkillProficiencyRow(
    skill: Skill,
    onSkillChange: (Skill) -> Unit
) {
    val proficiencyOptions = listOf("Beginner", "Intermediate", "Advanced", "Expert")
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Checkbox added here
        Checkbox(
            checked = skill.isSelected,
            onCheckedChange = { onSkillChange(skill.copy(isSelected = it)) }
        )

        OutlinedTextField(
            value = skill.name,
            onValueChange = { onSkillChange(skill.copy(name = it)) },
            modifier = Modifier.weight(1f),
            placeholder = { Text("Enter Skill") },
            shape = RoundedCornerShape(8.dp),
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.weight(0.7f)
        ) {
            OutlinedTextField(
                readOnly = true,
                value = skill.proficiency,
                onValueChange = {},
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor(),
                shape = RoundedCornerShape(8.dp),
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                proficiencyOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onSkillChange(skill.copy(proficiency = option))
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun NavigationButton(
    text: String,
    onClick: () -> Unit,
    leadingIcon: Painter? = null,
    trailingIcon: Painter? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, DividerColor),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (leadingIcon != null) {
                Icon(leadingIcon, contentDescription = null, tint = MediumGrayText)
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text,
                modifier = Modifier.weight(1f),
                color = MediumGrayText,
                fontWeight = FontWeight.SemiBold
            )
            if (trailingIcon != null) {
                Icon(trailingIcon, contentDescription = null, tint = MediumGrayText)
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewSkillsExpertiseScreen() {
    SkillsExpertiseScreen(navController = rememberNavController())
}