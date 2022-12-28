package com.example.apptracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.apptracker.util.navigation.MainNavItem
import com.example.apptracker.ui.routes.more.AddAppsPage
import com.example.apptracker.ui.routes.AppsPage
import com.example.apptracker.ui.routes.more.categories.CategoriesPage
import com.example.apptracker.ui.routes.more.MorePage
import com.example.apptracker.ui.routes.settings.appearance.AppearancePage
import com.example.apptracker.ui.routes.settings.SettingsPage
import com.example.apptracker.ui.routes.settings.appearance.AppearanceViewModel
import com.example.apptracker.ui.theme.AppTrackerTheme
import com.example.apptracker.util.data.getDatabase
import com.example.apptracker.util.navigation.Route
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        setContent {

            val appDatabase = getDatabase(applicationContext)

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
                            startDestination = Route.Apps.path,
                            enterTransition = { fadeInTransition },
                            popEnterTransition = { fadeInTransition },
                            exitTransition = { fadeOutTransition },
                            popExitTransition = { fadeOutTransition },
                        ) {
                            composable(Route.Apps.path) {
                                LaunchedEffect(Unit) {
                                    bottomBarState.value = true
                                }
                                //setSystemBarsAppearance()
                                AppsPage(
                                    navController = navController
                                )
                            }
                            composable(Route.More.path) {
                                LaunchedEffect(Unit) {
                                    bottomBarState.value = true
                                }
                                //setSystemBarsAppearance()
                                MorePage(
                                    navController = navController
                                )
                            }
                            composable(Route.AddApps.path) {
                                LaunchedEffect(Unit) {
                                    bottomBarState.value = false
                                }
                                //setSystemBarsAppearance()
                                AddAppsPage(
                                    navController = navController
                                )
                            }
                            composable(Route.Settings.path) {
                                LaunchedEffect(Unit) {
                                    bottomBarState.value = false
                                }
                                //setSystemBarsAppearance()
                                SettingsPage(
                                    navController = navController
                                )
                            }
                            composable(Route.Categories.path) {
                                LaunchedEffect(Unit) {
                                    bottomBarState.value = false
                                }
                                CategoriesPage(
                                    navController = navController,
                                    database = appDatabase
                                )
                            }
                            composable(Route.Appearance.path) {
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
        NavigationBar(
            //tonalElevation = 1.dp
        ) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            navBarItems.forEach { navItem ->
                val itemName = stringResource(id = navItem.title)
                NavigationBarItem(
                    icon = { Icon(painter = painterResource(id = navItem.icon), contentDescription = itemName) },
                    label = { Text(itemName) },
                    selected = currentRoute == navItem.route.path,
                    onClick = {
                        navController.navigate(navItem.route.path) {
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