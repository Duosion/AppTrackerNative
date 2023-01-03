package com.example.apptracker.ui.routes.more.addApps

import android.content.pm.ApplicationInfo
import com.example.apptracker.util.data.apps.TrackedApp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

data class AddAppsScreenState(
    val apps: List<ApplicationInfo> = listOf(),
    val trackedApps: Flow<List<TrackedApp>> = MutableStateFlow(listOf()),
    val queryState: AddAppsViewQueryState = AddAppsViewQueryState(),
    val isLoading: Boolean = false
)
