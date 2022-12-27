package com.example.apptracker.ui.routes.settings.appearance

import com.example.apptracker.util.data.settings.Setting
import com.example.apptracker.util.data.settings.values.DarkModeValues
import com.example.apptracker.util.data.settings.values.DynamicColorModeValues
import com.example.apptracker.util.data.settings.values.OledModeValues

data class AppearanceScreenState (
    val darkModeValue: DarkModeValues = DarkModeValues.fromId(Setting.DarkMode.defaultValue),
    val oledModeValue: OledModeValues = OledModeValues.fromId(Setting.OledMode.defaultValue),
    val dynamicColorModeValue: DynamicColorModeValues = DynamicColorModeValues.fromId(Setting.DynamicColorMode.defaultValue),
    val isLoading: Boolean = false
)