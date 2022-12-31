package com.example.apptracker.ui.routes.apps.addApp

import com.example.apptracker.util.data.apps.TrackedApp
import com.example.apptracker.util.data.categories.Category

data class AppScreenState (
    val trackedApp: TrackedApp,
    val categories: List<Category> = listOf(),
    val isLoading: Boolean = false
)