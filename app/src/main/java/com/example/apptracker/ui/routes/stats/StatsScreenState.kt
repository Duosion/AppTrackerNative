package com.example.apptracker.ui.routes.stats

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

data class StatsScreenState (
    val apps: Flow<List<StatsScreenApp>> = MutableStateFlow(listOf()),
    val isLoading: Boolean = false
)