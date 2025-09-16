package com.example.pathfinder.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

object RetrofitClient {
    // IMPORTANT: Replace with your deployed server's URL or local IP for testing.
    // For testing on an emulator, use https://revvote.site (as the chatbot backend is currently hosted on this domain)
    private const val BASE_URL = "https://revvote.site/"

    private val json = Json { ignoreUnknownKeys = true }

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(ApiService::class.java)
    }
}