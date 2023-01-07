package com.example.apptracker.ui.routes.stats

import com.example.apptracker.util.apps.GroupedUsageTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

data class StatsScreenState (
    val usageTime: List<GroupedUsageTime> = listOf(),
    val isLoading: Boolean = false
)