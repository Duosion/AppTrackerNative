package com.example.apptracker.util.data.settings.values

import com.example.apptracker.R
import com.example.apptracker.util.data.settings.ISettingValue

enum class OledModeValues(override val id: Int, override val valueName: Int) : ISettingValue {
    OFF(0, R.string.settings_dark_mode_off_value_name),
    ON(1, R.string.settings_dark_mode_on_value_name);

    companion object {
        fun fromId (id: Int): OledModeValues {
            return when(id) {
                0 -> OledModeValues.OFF
                else -> OledModeValues.ON
            }
        }
    }
}