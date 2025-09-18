package com.example.pathfinder.network

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.pathfinder.model.ChatMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

// Create a DataStore instance at the top level
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "chat_history")

class ChatHistoryRepository(private val context: Context) {

    private val historyKey = stringPreferencesKey("chat_messages")

    // Flow to read the chat history from DataStore
    val chatHistory: Flow<List<ChatMessage>> = context.dataStore.data
        .map { preferences ->
            val jsonString = preferences[historyKey]
            if (jsonString != null) {
                Json.decodeFromString<List<ChatMessage>>(jsonString)
            } else {
                emptyList()
            }
        }

    suspend fun clearChatHistory() {
        context.dataStore.edit { preferences ->
            preferences.clear() // Removes all data
        }
    }

    // Function to save the chat history to DataStore
    suspend fun saveChatHistory(messages: List<ChatMessage>) {
        context.dataStore.edit { preferences ->
            val jsonString = Json.encodeToString(messages)
            preferences[historyKey] = jsonString
        }
    }
}