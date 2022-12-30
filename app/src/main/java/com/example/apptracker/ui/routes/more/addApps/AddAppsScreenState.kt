package com.example.apptracker.ui.routes.more.addApps

import android.content.pm.ApplicationInfo

data class AddAppsScreenState(
    val apps: List<ApplicationInfo> = listOf(),
    val queryState: AddAppsViewQueryState = AddAppsViewQueryState(),
    val isLoading: Boolean = false
)
