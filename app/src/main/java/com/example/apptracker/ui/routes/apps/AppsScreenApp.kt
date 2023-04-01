package com.example.apptracker.ui.routes.apps

import android.content.pm.ApplicationInfo
import android.graphics.drawable.Drawable
import androidx.compose.ui.graphics.painter.Painter
import com.example.apptracker.util.data.apps.TrackedApp

data class AppsScreenApp(
    val trackedApp: TrackedApp,
    val appInfo: ApplicationInfo? = null,
    val label: String? = null,
    val icon: Drawable? = null,
    var painter: Painter? = null
)
