package com.example.pathfinder.model

data class SkillDetailUI(
    val id: String,
    val name: String,
    val description: String,
    val relatedRoles: List<String>,
    val courses: List<Pair<String, String>>
)
