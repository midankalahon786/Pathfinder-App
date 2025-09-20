package com.example.pathfinder.network.data

import kotlinx.serialization.Serializable

@Serializable
data class ChatRequest(
    val userId: String, // ADDED
    val prompt: String
)

@Serializable
data class ChatResponse(
    val response: String
)

@Serializable
data class Content(
    val role: String,
    val parts: List<Part>
)

@Serializable
data class Part(
    val text: String
)
