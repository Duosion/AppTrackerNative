package com.example.apptracker

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.ViewCompat
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.apptracker.util.navigation.MainNavItem
import com.example.apptracker.util.navigation.MoreListItem
import com.example.apptracker.ui.routes.AddAppsPage
import com.example.apptracker.ui.routes.AppsPage
import com.example.apptracker.ui.routes.MorePage
import com.example.apptracker.ui.routes.settings.appearance.AppearancePage
import com.example.apptracker.ui.routes.settings.SettingsPage
import com.example.apptracker.ui.routes.settings.appearance.AppearanceViewModel
import com.example.apptracker.ui.theme.AppTrackerTheme
import com.example.apptracker.util.data.getDatabase
import com.example.apptracker.util.data.settings.Setting
import com.example.apptracker.util.data.settings.values.DarkModeValues
import com.example.apptracker.util.navigation.SettingsListItem
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            val appDatabase = getDatabase(applicationContext)

            val isDarkTheme = isSystemInDarkTheme()
            val view = LocalView.current

            fun setSystemBarsAppearance(
                darkStatusBar: Boolean? = !isDarkTheme,
                darkNavigationBar: Boolean? = !isDarkTheme
            ) {
                // changes the system bars appearance depending on the passed variables
                if (Build.VERSION.SDK_INT >= 30) {
                    val insetsController = window.insetsController
                    window.setDecorFitsSystemWindows(false)
                    if (!darkStatusBar!!) {
                        insetsController?.setSystemBarsAppearance(
                            0,
                            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                        )
                    } else {
                        insetsController?.setSystemBarsAppearance(
                            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                        )
                    }
                    if (!darkNavigationBar!!) {
                        insetsController?.setSystemBarsAppearance(
                            0,
                            WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
                        )
                    } else {
                        insetsController?.setSystemBarsAppearance(
                            WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS,
                            WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
                        )
                    }
                } else {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE

                    ViewCompat.getWindowInsetsController(view)?.isAppearanceLightNavigationBars = darkNavigationBar!!
                    ViewCompat.getWindowInsetsController(view)?.isAppearanceLightStatusBars = darkStatusBar!!
                }
            }

            setSystemBarsAppearance()

            val appearanceViewModel = AppearanceViewModel(appDatabase)

            AppTrackerTheme(
                database = appDatabase,
                viewModel = appearanceViewModel
            ) {
                val transitionTweenTime = 350 // the transition tween time in ms
                val fadeInTransition = fadeIn(animationSpec = tween(durationMillis = transitionTweenTime))
                val fadeOutTransition = fadeOut(animationSpec = tween(durationMillis = transitionTweenTime))

                val navController = rememberAnimatedNavController()
                val bottomBarState = remember { mutableStateOf(false) }
                Scaffold(
                    bottomBar = {
                        BottomBar(
                            navController = navController,
                            bottomBarState = bottomBarState
                        )
                    },
                    content = { padding ->
                        AnimatedNavHost(
                            modifier = Modifier.padding(bottom = padding.calculateBottomPadding()),
                            navController = navController,
                            startDestination = MainNavItem.Apps.route,
                            enterTransition = { fadeInTransition },
                            popEnterTransition = { fadeInTransition },
                            exitTransition = { fadeOutTransition },
                            popExitTransition = { fadeOutTransition },
                        ) {
                            composable(MainNavItem.Apps.route) {
                                LaunchedEffect(Unit) {
                                    bottomBarState.value = true
                                }
                                //setSystemBarsAppearance()
                                AppsPage(
                                    navController = navController
                                )
                            }
                            composable(MainNavItem.More.route) {
                                LaunchedEffect(Unit) {
                                    bottomBarState.value = true
                                }
                                //setSystemBarsAppearance()
                                MorePage(
                                    navController = navController
                                )
                            }
                            composable(MoreListItem.AddApps.route) {
                                LaunchedEffect(Unit) {
                                    bottomBarState.value = false
                                }
                                //setSystemBarsAppearance()
                                AddAppsPage(
                                    navController = navController
                                )
                            }
                            composable(MoreListItem.Settings.route) {
                                LaunchedEffect(Unit) {
                                    bottomBarState.value = false
                                }
                                //setSystemBarsAppearance()
                                SettingsPage(
                                    navController = navController
                                )
                            }
                            composable(SettingsListItem.Appearance.route) {
                                LaunchedEffect(Unit) {
                                    bottomBarState.value = false
                                }
                                //setSystemBarsAppearance()
                                AppearancePage(
                                    navController = navController,
                                    database = appDatabase,
                                    viewModel = appearanceViewModel
                                )
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun BottomBar(
    navController: NavController,
    bottomBarState: MutableState<Boolean>
) {
    val navBarItems = listOf(
        MainNavItem.Apps,
        MainNavItem.More
    )

    AnimatedVisibility(
        visible = bottomBarState.value,
        enter = slideInVertically { it },
        exit = slideOutVertically { it }
    ) {
        NavigationBar {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            navBarItems.forEach { navItem ->
                val itemName = stringResource(id = navItem.title)
                NavigationBarItem(
                    icon = { Icon(painter = painterResource(id = navItem.icon), contentDescription = itemName) },
                    label = { Text(itemName) },
                    selected = currentRoute == navItem.route,
                    onClick = {
                        navController.navigate(navItem.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}