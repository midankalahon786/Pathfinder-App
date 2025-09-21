package com.example.pathfinder.ui.screens.fake

import com.example.pathfinder.ui.IThemeViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * A fake implementation of the IThemeViewModel for use in Jetpack Compose previews.
 */
class FakeThemeViewModel : IThemeViewModel {
    // Default the preview to light mode (or set to true to preview dark mode)
    override val isDarkTheme: StateFlow<Boolean> = MutableStateFlow(false)

    // The function does nothing in a static preview
    override fun setTheme(isDark: Boolean) {}
}
