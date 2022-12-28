package com.example.apptracker.ui.routes.settings.appearance

import com.example.apptracker.util.data.settings.Setting
import com.example.apptracker.util.data.settings.values.DarkModeValues
import com.example.apptracker.util.data.settings.values.OledModeValues
import com.example.apptracker.util.data.settings.values.ThemeValues

data class AppearanceScreenState (
    val darkModeValue: DarkModeValues = DarkModeValues.fromId(Setting.DarkMode.defaultValue),
    val oledModeValue: OledModeValues = OledModeValues.fromId(Setting.OledMode.defaultValue),
    val themeValue: ThemeValues = ThemeValues.fromId(Setting.Theme.defaultValue),
    val isLoading: Boolean = false
)