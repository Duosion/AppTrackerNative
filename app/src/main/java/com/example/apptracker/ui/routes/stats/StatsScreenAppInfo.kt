package com.example.apptracker.ui.routes.stats

import android.content.pm.ApplicationInfo
import android.graphics.drawable.Drawable

data class StatsScreenAppInfo (
    val appInfo: ApplicationInfo? = null,
    val label: String? = null,
    val icon: Drawable? = null
)