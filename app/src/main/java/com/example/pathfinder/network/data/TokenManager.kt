package com.example.pathfinder.network.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class TokenManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val AUTH_TOKEN = "auth_token"
    }

    fun saveToken(token: String) {
        prefs.edit { putString(AUTH_TOKEN, token) }
    }

    fun getToken(): String? {
        return prefs.getString(AUTH_TOKEN, null)
    }

    fun clearToken() {
        prefs.edit { remove(AUTH_TOKEN) }
    }
}