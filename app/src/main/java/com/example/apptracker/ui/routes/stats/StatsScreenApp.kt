package com.example.apptracker.ui.routes.stats

import android.content.pm.ApplicationInfo
import android.graphics.drawable.Drawable
import com.example.apptracker.util.data.apps.TrackedAppUsageTime

data class StatsScreenApp (
    val usageInfo: TrackedAppUsageTime,
    val appInfo: ApplicationInfo? = null,
    val label: String? = null,
    val icon: Drawable? = null
)