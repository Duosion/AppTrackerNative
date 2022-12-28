package com.example.apptracker.util.navigation

import androidx.compose.ui.res.stringResource
import com.example.apptracker.R

sealed class MainNavItem (val title: Int, val icon: Int, val route: Route) {
    object Apps: MainNavItem(R.string.apps_nav_bar_button_name, R.drawable.apps_nav_bar_icon, Route.Apps)
    object More: MainNavItem(R.string.more_nav_bar_button_name, R.drawable.more_nav_bar_icon,Route.More)
}
