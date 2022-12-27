package com.example.apptracker.util.data.settings.values

import com.example.apptracker.R
import com.example.apptracker.util.data.settings.ISettingValue

enum class DynamicColorModeValues(override val id: Int, override val valueName: Int) : ISettingValue {
    OFF(0, R.string.settings_dynamic_color_mode_off_value_name),
    ON(1, R.string.settings_dynamic_color_mode_on_value_name);

    companion object {
        fun fromId (id: Int): DynamicColorModeValues {
            return when(id) {
                0 -> DynamicColorModeValues.OFF
                else -> DynamicColorModeValues.ON
            }
        }
    }
}