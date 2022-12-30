package com.example.apptracker.ui.routes.apps.addApp

import com.example.apptracker.util.data.apps.TrackedApp
import com.example.apptracker.util.data.categories.Category
import java.time.LocalTime

data class AddAppScreenState (
    val trackedApp: TrackedApp,
    val categories: List<Category> = listOf(),
)