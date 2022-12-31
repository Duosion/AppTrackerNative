package com.example.apptracker.ui.routes.apps

import com.example.apptracker.util.data.apps.TrackedApp
import com.example.apptracker.util.data.categories.Category

data class AppsScreenState(
    val apps: List<AppsScreenApp> = listOf(),
    val categories: List<Category> = listOf(),
    val isLoading: Boolean = false
)
