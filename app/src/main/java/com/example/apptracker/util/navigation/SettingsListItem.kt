package com.example.apptracker.util.navigation

import com.example.apptracker.R

sealed class SettingsListItem (val headline: Int, val supporting: Int, val icon: Int, val route: Route) {
    object Appearance: SettingsListItem(R.string.settings_appearance_title, R.string.settings_appearance_supporting, R.drawable.appearance_icon, Route.Appearance)
    object General: SettingsListItem(R.string.settings_general_title, R.string.settings_general_supporting, R.drawable.tune_icon, Route.General)
}

