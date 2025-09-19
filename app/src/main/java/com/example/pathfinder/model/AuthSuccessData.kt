package com.example.pathfinder.model

data class AuthSuccessData(
    val token: String,
    val userId: String,
    val userName: String?,
    val userEmail: String?
)
