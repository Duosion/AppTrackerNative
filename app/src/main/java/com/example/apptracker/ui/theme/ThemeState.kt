package com.example.apptracker.ui.theme

import com.example.apptracker.util.data.settings.Setting
import com.example.apptracker.util.data.settings.values.DarkModeValues
import com.example.apptracker.util.data.settings.values.DynamicColorModeValues
import com.example.apptracker.util.data.settings.values.OledModeValues

data class ThemeState(
    val darkModeValue: DarkModeValues = DarkModeValues.fromId(Setting.DarkMode.defaultValue),
    val oledModeValue: OledModeValues = OledModeValues.fromId(Setting.OledMode.defaultValue),
    val dynamicColorModeValue: DynamicColorModeValues = DynamicColorModeValues.fromId(Setting.DynamicColorMode.defaultValue)
)