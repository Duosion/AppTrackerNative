package com.example.apptracker.util.data.settings.values

import com.example.apptracker.R
import com.example.apptracker.util.data.settings.ISettingValue

enum class ThemeValues(override val id: Int, override val valueName: Int) : ISettingValue {
    DYNAMIC(0,R.string.theme_dynamic_label),
    GREEN_APPLE(1,R.string.theme_greenapple_label),
    LAVENDER(2,R.string.theme_lavender_label),
    MIDNIGHT_DUSK(3, R.string.theme_midnightdusk_label),
    STRAWBERRY(4, R.string.theme_strawberry_label),
    TAKO(5, R.string.theme_tako_label),
    TEAL_TURQUOISE(6, R.string.theme_tealturquoise_label),
    TIDAL_WAVE(7, R.string.theme_tidalwave_label),
    YIN_YANG(8, R.string.theme_yinyang_label),
    YOTSUBA(9, R.string.theme_yotsuba_label);

    companion object {
        private val map = values().associateBy { it.id }
        fun fromId (id: Int) = map[id] ?: DYNAMIC
    }
}