package com.example.pathfinder.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

interface IThemeViewModel {
    /**
     * A flow that emits true if the dark theme is enabled, false otherwise.
     */
    val isDarkTheme: StateFlow<Boolean>

    /**
     * Sets the theme preference.
     * @param isDark True to enable dark mode, false to disable it.
     */
    fun setTheme(isDark: Boolean)
}


class ThemeViewModel(application: Application) : AndroidViewModel(application), IThemeViewModel {
    private val themeManager = ThemeManager(application)

    // Expose the theme setting as a StateFlow for the UI to observe
    override val isDarkTheme = themeManager.themeFlow
        .stateIn(viewModelScope, SharingStarted.Lazily, false)

    // Function for the UI to call to change the theme
    override fun setTheme(isDark: Boolean) {
        viewModelScope.launch {
            themeManager.setTheme(isDark)
        }
    }
}