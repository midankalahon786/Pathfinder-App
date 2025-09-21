package com.example.pathfinder.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pathfinder.graphql.GetSkillDetailsByNameQuery
import com.example.pathfinder.network.apolloClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SkillDetailUI(
    val id: String,
    val name: String,
    val description: String,
    val relatedRoles: List<String>,
    val courses: List<Pair<String, String>>
)
