package com.example.pathfinder.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pathfinder.model.ChatMessage
import com.example.pathfinder.model.UserProfile
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdvisorViewModel : ViewModel() {

    // --- 1. HARDCODED PROFILE DATA ---
    private val _userProfile = MutableStateFlow(
        UserProfile(
            name = "Alex",
            currentRole = "Marketing Manager",
            skills = listOf("SEO", "Content Writing", "Social Media")
        )
    )
    val userProfile = _userProfile.asStateFlow()
    // --- End of new code ---

    private val _messages = MutableStateFlow<List<ChatMessage>>(listOf())
    val messages = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        // Initial welcome message from the AI
        viewModelScope.launch {
            _isLoading.value = true
            delay(1000) // Simulate network call

            // --- 2. PERSONALIZED WELCOME MESSAGE ---
            val welcomeMessage = "Hello, ${_userProfile.value.name}! I'm your Pathfinder AI. " +
                    "I see you're a ${_userProfile.value.currentRole}. How can I help with your career goals today?"

            _messages.value = listOf(
                ChatMessage(welcomeMessage, isFromUser = false)
            )
            _isLoading.value = false
        }
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return

        // Add user message to the list
        _messages.value = _messages.value + ChatMessage(text, isFromUser = true)

        // Simulate a response from the AI
        viewModelScope.launch {
            _isLoading.value = true
            delay(2000) // Simulate network call and AI thinking time

            // The AI response function now knows about the user's profile
            val aiResponse = getDummyAiResponse(text, _userProfile.value)
            _messages.value = _messages.value + ChatMessage(aiResponse, isFromUser = false)
            _isLoading.value = false
        }
    }

    // --- 3. CONTEXT-AWARE AI RESPONSES ---
    private fun getDummyAiResponse(userMessage: String, profile: UserProfile): String {
        return when {
            "product manager" in userMessage.lowercase() -> {
                "Excellent choice! To become a Product Manager from a ${profile.currentRole}, you should focus on:\n" +
                        "1. **UX/UI Fundamentals:** Understanding user-centric design.\n" +
                        "2. **Agile Methodologies:** Learning Scrum and Kanban.\n" +
                        "3. **Technical Literacy:** Basic knowledge of how software is built.\n\n" +
                        "I can recommend some learning resources for these skills. Would you like that?"
            }
            "recommend" in userMessage.lowercase() -> {
                "Of course! For UX/UI, I recommend the 'Google UX Design Professional Certificate' on Coursera. For Agile, the 'Scrum for Beginners' course on Udemy is a great start."
            }
            "my skills" in userMessage.lowercase() -> {
                "Based on your profile, your current skills are: ${profile.skills.joinToString(", ")}. We can definitely build on these!"
            }
            else -> {
                "That's an interesting goal. As a ${profile.currentRole}, what's the first step you'd like to explore?"
            }
        }
    }
}
