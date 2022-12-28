package com.example.apptracker.util.data.settings.values

import com.example.apptracker.R
import com.example.apptracker.util.data.settings.ISettingValue

enum class ThemeValues(override val id: Int, override val valueName: Int) : ISettingValue {
    DYNAMIC(0,R.string.theme_dynamic_label),
    GREENAPPLE(1,R.string.theme_greenapple_label),
    LAVENDER(2,R.string.theme_lavender_label),
    MIDNIGHTDUSK(3, R.string.theme_midnightdusk_label),
    STRAWBERRY(4, R.string.theme_strawberry_label),
    TAKO(5, R.string.theme_tako_label),
    TEALTURQUOISE(6, R.string.theme_tealturquoise_label),
    TIDALWAVE(7, R.string.theme_tidalwave_label),
    YINYANG(8, R.string.theme_yinyang_label),
    YOTSUBA(9, R.string.theme_yotsuba_label);

    companion object {
        fun fromId (id: Int): ThemeValues {
            return when(id) {
                0 -> DYNAMIC
                1 -> GREENAPPLE
                2 -> LAVENDER
                3 -> MIDNIGHTDUSK
                4 -> STRAWBERRY
                5 -> TAKO
                6 -> TEALTURQUOISE
                7 -> TIDALWAVE
                8 -> YINYANG
                else -> YOTSUBA
            }
        }
    }
}