package com.example.apptracker.util.data.settings.values

import com.example.apptracker.R
import com.example.apptracker.util.data.apps.TrackedAppReminderOffset
import com.example.apptracker.util.data.settings.ISettingValue

enum class DarkModeValues(override val id: Int, override val valueName: Int) : ISettingValue {
    DEFAULT(0, R.string.settings_dark_mode_system_default_value_name),
    OFF(1, R.string.settings_dark_mode_off_value_name),
    ON(2, R.string.settings_dark_mode_on_value_name);

    companion object {
        private val map = TrackedAppReminderOffset.values().associateBy { it.id }
        fun fromId (id: Int) = map[id] ?: DEFAULT
    }
}