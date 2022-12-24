package com.example.apptracker.navigation

import com.example.apptracker.R

sealed class MoreListItem (val headline: Int, val supporting: Int, val icon: Int, val route: String) {
    object AddApps: MoreListItem(R.string.more_add_app_button_headline, R.string.more_add_app_button_supporting, R.drawable.add_app_icon, "/addApps")
}

