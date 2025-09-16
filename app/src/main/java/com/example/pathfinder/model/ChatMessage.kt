package com.example.pathfinder.model

import kotlinx.serialization.Serializable

@Serializable //
data class ChatMessage(
    val message: String,
    val isFromUser: Boolean
)
