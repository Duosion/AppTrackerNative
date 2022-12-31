package com.example.apptracker.ui.routes.more.addApps

import android.content.pm.ApplicationInfo
import com.example.apptracker.util.data.apps.TrackedApp

data class AddAppsScreenState(
    val apps: List<ApplicationInfo> = listOf(),
    val trackedApps: List<TrackedApp> = listOf(),
    val queryState: AddAppsViewQueryState = AddAppsViewQueryState(),
    val isLoading: Boolean = false
)
