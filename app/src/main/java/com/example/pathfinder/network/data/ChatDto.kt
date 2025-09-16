package com.example.pathfinder.network.data

import kotlinx.serialization.Serializable

@Serializable
data class ChatRequest(
    val prompt: String,
    val history: List<Content>
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
