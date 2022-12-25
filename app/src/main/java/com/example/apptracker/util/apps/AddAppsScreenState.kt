package com.example.apptracker.util.apps

import android.content.pm.ApplicationInfo

data class AddAppsScreenState(
    val apps: List<ApplicationInfo>? = null,
    val isLoading: Boolean = false
)
