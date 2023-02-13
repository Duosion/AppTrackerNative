package com.example.apptracker.util.data.settings

import com.example.apptracker.util.data.settings.values.DarkModeValues
import com.example.apptracker.util.data.settings.values.OledModeValues
import com.example.apptracker.util.data.settings.values.RandomThemeValues
import com.example.apptracker.util.data.settings.values.ThemeValues

sealed class Setting(val key: String, val defaultValue: Int) {
    object DarkMode: Setting("darkMode", DarkModeValues.DEFAULT.id)
    object OledMode: Setting("oledMode", OledModeValues.OFF.id)
    object Theme: Setting("theme", ThemeValues.DYNAMIC.id)
    object RandomTheme: Setting("randomTheme", RandomThemeValues.OFF.id)
}
