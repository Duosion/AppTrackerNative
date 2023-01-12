package com.example.apptracker.ui.routes.stats

import com.example.apptracker.util.apps.GroupedUsageTime

data class StatsScreenState (
    val usageTime: List<GroupedUsageTime> = listOf(),
    val allTimeUsageTime: GroupedUsageTime? = null,
    val appsInfo: Map<String, StatsScreenAppInfo> = mapOf(),
    val isLoading: Boolean = false
)