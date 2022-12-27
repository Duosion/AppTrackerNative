package com.example.apptracker.util.data.settings.values

import com.example.apptracker.R
import com.example.apptracker.util.data.settings.ISettingValue

enum class DarkModeValues(override val id: Int, override val valueName: Int) : ISettingValue {
    OFF(0, R.string.settings_dark_mode_off_value_name),
    ON(1, R.string.settings_dark_mode_on_value_name),
    DEFAULT(2, R.string.settings_dark_mode_system_default_value_name);

    companion object {
        fun fromId (id: Int): DarkModeValues {
            return when(id) {
                0 -> DarkModeValues.OFF
                1 -> DarkModeValues.ON
                else -> DarkModeValues.DEFAULT
            }
        }
    }
}