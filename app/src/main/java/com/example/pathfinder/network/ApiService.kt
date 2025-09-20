package com.example.pathfinder.network

import com.example.pathfinder.network.data.ChatRequest
import com.example.pathfinder.network.data.ChatResponse
import retrofit2.http.Body
import retrofit2.http.POST


interface ApiService {
    @POST("/chat") // The endpoint path from your server.js
    suspend fun sendMessage(@Body request: ChatRequest): ChatResponse
}