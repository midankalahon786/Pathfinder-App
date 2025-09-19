package com.example.pathfinder.network.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class TokenManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val AUTH_TOKEN = "auth_token"
        private const val USER_ID = "user_id"
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

    fun saveUserId(userId: String) {
        prefs.edit { putString(USER_ID, userId) }
    }

    fun getUserId(): String? {
        return prefs.getString(USER_ID, null)
    }

    fun clear() {
        prefs.edit { remove(AUTH_TOKEN).remove(USER_ID) }
    }
}