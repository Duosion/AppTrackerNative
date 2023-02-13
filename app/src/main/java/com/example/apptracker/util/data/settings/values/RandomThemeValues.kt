package com.example.apptracker.util.data.settings.values

import com.example.apptracker.R
import com.example.apptracker.util.data.settings.ISettingValue

enum class RandomThemeValues(override val id: Int, override val valueName: Int) : ISettingValue {
    OFF(0, R.string.settings_dark_mode_off_value_name),
    ON(1, R.string.settings_dark_mode_on_value_name);

    companion object {
        private val map = values().associateBy { it.id }
        fun fromId (id: Int) = map[id] ?: OFF
    }
}