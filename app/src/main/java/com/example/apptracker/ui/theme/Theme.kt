package com.example.apptracker.ui.theme

import android.app.Activity
import android.content.res.Resources.Theme
import android.os.Build
import android.view.View
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.ViewCompat
import com.example.apptracker.ui.routes.settings.appearance.AppearanceViewModel
import com.example.apptracker.util.data.AppDatabase
import com.example.apptracker.util.data.settings.values.DarkModeValues
import com.example.apptracker.util.data.settings.values.DynamicColorModeValues
import com.example.apptracker.util.data.settings.values.OledModeValues

private val DarkColorScheme = darkColorScheme(
        primary = Purple80,
        secondary = PurpleGrey80,
        tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
        primary = Purple40,
        secondary = PurpleGrey40,
        tertiary = Pink40

        /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

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

    val dynamicColor = when(state.dynamicColorModeValue) {
        DynamicColorModeValues.OFF -> false
        DynamicColorModeValues.ON -> true
    }

    var colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
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