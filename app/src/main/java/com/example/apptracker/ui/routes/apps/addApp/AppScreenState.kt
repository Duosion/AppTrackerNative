package com.example.apptracker.ui.routes.apps.addApp

import com.example.apptracker.util.data.apps.TrackedApp
import com.example.apptracker.util.data.categories.Category
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

data class AppScreenState (
    val trackedApp: Flow<TrackedApp?>,
    val categories: List<Category> = listOf(),
    val isLoading: Boolean = false
)