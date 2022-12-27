package com.example.apptracker.util.data.settings

import com.example.apptracker.util.data.settings.values.DarkModeValues
import com.example.apptracker.util.data.settings.values.DynamicColorModeValues
import com.example.apptracker.util.data.settings.values.OledModeValues

sealed class Setting(val key: String, val defaultValue: Int) {
    object DarkMode: Setting("darkMode", DarkModeValues.DEFAULT.id)
    object OledMode: Setting("oledMode", OledModeValues.OFF.id)
    object DynamicColorMode: Setting("dynamicColorMode", DynamicColorModeValues.ON.id)
}
