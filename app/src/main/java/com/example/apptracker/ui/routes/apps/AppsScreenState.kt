package com.example.apptracker.ui.routes.apps

import com.example.apptracker.util.data.categories.Category
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

data class AppsScreenState(
    val apps: Flow<List<AppsScreenApp>> = MutableStateFlow(listOf()),
    val categories: Flow<List<Category>> = MutableStateFlow(listOf()),
    val isLoading: Boolean = false
)
