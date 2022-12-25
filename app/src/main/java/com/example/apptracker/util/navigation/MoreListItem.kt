package com.example.apptracker.util.navigation

import com.example.apptracker.R

sealed class MoreListItem (val headline: Int, val supporting: Int, val icon: Int, val route: String) {
    object AddApps: MoreListItem(R.string.more_add_app_button_headline, R.string.more_add_app_button_supporting, R.drawable.add_app_icon, "/more/addApps")
    object Settings: MoreListItem(R.string.settings_title,R.string.more_settings_button_supporting,R.drawable.settings_icon,"/more/settings")
}

