// In viewmodel/AdvisorViewModel.kt
package com.example.pathfinder.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.pathfinder.model.ChatMessage
import com.example.pathfinder.network.ChatHistoryRepository
import com.example.pathfinder.network.RetrofitClient
import com.example.pathfinder.network.data.ChatRequest
import com.example.pathfinder.network.data.Content
import com.example.pathfinder.network.data.Part
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AdvisorViewModel(private val repository: ChatHistoryRepository) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        // Load history when the ViewModel is created
        loadChatHistory()
    }

    private fun loadChatHistory() {
        viewModelScope.launch {
            _messages.value = repository.chatHistory.first()
        }
    }

    // History for the Gemini API
    private val conversationHistory = mutableListOf<Content>()

    fun sendMessage(text: String) {
        if (text.isBlank() || _isLoading.value) return

        val userMessage = ChatMessage(message = text, isFromUser = true)

        // Add user message and save the updated list
        val updatedMessages = _messages.value + userMessage
        _messages.value = updatedMessages
        viewModelScope.launch { repository.saveChatHistory(updatedMessages) }

        _isLoading.value = true
        viewModelScope.launch {
            try {
                // Prepare history for the API from the current message list
                val apiHistory = _messages.value.map {
                    val role = if (it.isFromUser) "user" else "model"
                    Content(role = role, parts = listOf(Part(it.message)))
                }

                val request = ChatRequest(prompt = text, history = apiHistory)
                val response = RetrofitClient.instance.sendMessage(request)
                val aiMessage = ChatMessage(message = response.response, isFromUser = false)

                // Add AI response and save again
                val finalMessages = _messages.value + aiMessage
                _messages.value = finalMessages
                repository.saveChatHistory(finalMessages)

            } catch (e: Exception) {
                val errorMessage = ChatMessage("Sorry, something went wrong: ${e.message}", false)
                val errorMessages = _messages.value + errorMessage
                _messages.value = errorMessages
                repository.saveChatHistory(errorMessages)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearChatHistory() {
        viewModelScope.launch {
            repository.clearChatHistory()
            _messages.value = emptyList() // Clear the messages in the UI
        }
    }
}

class AdvisorViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdvisorViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AdvisorViewModel(ChatHistoryRepository(application)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}