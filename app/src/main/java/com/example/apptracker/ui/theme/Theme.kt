package com.example.apptracker.ui.theme

import android.app.Activity
import android.os.Build
import android.view.View
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.ViewCompat
import com.example.apptracker.ui.routes.settings.appearance.AppearanceViewModel
import com.example.apptracker.ui.theme.greenapple.darkGreenAppleColorScheme
import com.example.apptracker.ui.theme.greenapple.lightGreenAppleColorScheme
import com.example.apptracker.ui.theme.lavender.darkLavenderColorScheme
import com.example.apptracker.ui.theme.lavender.lightLavenderColorScheme
import com.example.apptracker.ui.theme.midnightdusk.darkMidnightDuskColorScheme
import com.example.apptracker.ui.theme.midnightdusk.lightMidnightDuskColorScheme
import com.example.apptracker.ui.theme.strawberry.darkStrawberryColorScheme
import com.example.apptracker.ui.theme.strawberry.lightStrawberryColorScheme
import com.example.apptracker.ui.theme.tako.darkTakoColorScheme
import com.example.apptracker.ui.theme.tako.lightTakoColorScheme
import com.example.apptracker.ui.theme.tealturquoise.darkTealTurquoiseColorScheme
import com.example.apptracker.ui.theme.tealturquoise.lightTealTurquoiseColorScheme
import com.example.apptracker.ui.theme.tidalwave.darkTidalWaveColorScheme
import com.example.apptracker.ui.theme.tidalwave.lightTidalWaveColorScheme
import com.example.apptracker.ui.theme.yinyang.darkYinYangColorScheme
import com.example.apptracker.ui.theme.yinyang.lightYinYangColorScheme
import com.example.apptracker.ui.theme.yotsuba.darkYotsubaColorScheme
import com.example.apptracker.ui.theme.yotsuba.lightYotsubaColorScheme
import com.example.apptracker.util.data.AppDatabase
import com.example.apptracker.util.data.settings.values.DarkModeValues
import com.example.apptracker.util.data.settings.values.OledModeValues
import com.example.apptracker.util.data.settings.values.ThemeValues

private val OledDarkColorScheme = darkColorScheme(
    background = Color(0xFF000000),
    surface = Color(0xFF000000)
)

@Composable
fun AppTrackerTheme(
        // Dynamic color is available on Android 12+
        database: AppDatabase,
        viewModel: AppearanceViewModel = AppearanceViewModel(database),
        content: @Composable () -> Unit
) {
    val state by viewModel.state.collectAsState()

    val darkTheme = when(state.darkModeValue) {
        DarkModeValues.OFF -> false
        DarkModeValues.ON -> true
        DarkModeValues.DEFAULT -> isSystemInDarkTheme()
    }

    var colorScheme = when(state.themeValue) {
        ThemeValues.DYNAMIC -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        ThemeValues.GREENAPPLE -> if (darkTheme) darkGreenAppleColorScheme else lightGreenAppleColorScheme
        ThemeValues.LAVENDER -> if (darkTheme) darkLavenderColorScheme else lightLavenderColorScheme
        ThemeValues.MIDNIGHTDUSK -> if (darkTheme) darkMidnightDuskColorScheme else lightMidnightDuskColorScheme
        ThemeValues.STRAWBERRY -> if (darkTheme) darkStrawberryColorScheme else lightStrawberryColorScheme
        ThemeValues.TAKO -> if (darkTheme) darkTakoColorScheme else lightTakoColorScheme
        ThemeValues.TEALTURQUOISE -> if (darkTheme) darkTealTurquoiseColorScheme else lightTealTurquoiseColorScheme
        ThemeValues.TIDALWAVE -> if (darkTheme) darkTidalWaveColorScheme else lightTidalWaveColorScheme
        ThemeValues.YINYANG -> if (darkTheme) darkYinYangColorScheme else lightYinYangColorScheme
        ThemeValues.YOTSUBA -> if (darkTheme) darkYotsubaColorScheme else lightYotsubaColorScheme
    }

    if (darkTheme && state.oledModeValue == OledModeValues.ON) {
        colorScheme = colorScheme.copy(background = OledDarkColorScheme.background, surface = OledDarkColorScheme.surface)
    }

    val view = LocalView.current
    val window = (view.context as Activity).window
    if (!view.isInEditMode) {
        SideEffect {
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()

            if (Build.VERSION.SDK_INT >= 30) {
                val insetsController = window.insetsController
                window.setDecorFitsSystemWindows(false)
                // status bar
                insetsController?.setSystemBarsAppearance(
                    if (darkTheme) 0 else WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                )
                // nav
                insetsController?.setSystemBarsAppearance(
                    if (darkTheme) 0 else WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS,
                    WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
                )
            } else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE

                ViewCompat.getWindowInsetsController(view)?.isAppearanceLightNavigationBars = darkTheme
                ViewCompat.getWindowInsetsController(view)?.isAppearanceLightStatusBars = darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}