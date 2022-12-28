package com.example.apptracker.util.navigation

import com.example.apptracker.R

sealed class MoreListItem (val headline: Int, val supporting: Int, val icon: Int, val route: Route) {
    object AddApps: MoreListItem(R.string.more_add_app_button_headline, R.string.more_add_app_button_supporting, R.drawable.add_app_icon, Route.AddApps)
    object Settings: MoreListItem(R.string.settings_title,R.string.more_settings_button_supporting,R.drawable.settings_icon, Route.Settings)
    object Categories: MoreListItem(R.string.more_category_button_headline, R.string.more_category_button_supporting, R.drawable.label_icon, Route.Categories)
}

