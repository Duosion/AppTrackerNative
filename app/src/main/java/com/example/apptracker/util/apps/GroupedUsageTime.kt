package com.example.apptracker.util.apps

import com.example.apptracker.util.data.apps.TrackedAppUsageTime
import java.time.LocalDateTime

data class GroupedUsageTime(
    val timestamp: UsageTimeTimestamp,
    val combinedUsageTime: Long,
    val values: List<TrackedAppUsageTime>
)
