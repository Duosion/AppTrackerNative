package com.example.apptracker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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
import com.example.apptracker.ui.routes.PackageUsagePermissionPage
import com.example.apptracker.ui.routes.more.categories.CategoriesPage
import com.example.apptracker.ui.routes.more.MorePage
import com.example.apptracker.ui.routes.settings.appearance.AppearancePage
import com.example.apptracker.ui.routes.settings.SettingsPage
import com.example.apptracker.ui.routes.settings.appearance.AppearanceViewModel
import com.example.apptracker.ui.theme.AppTrackerTheme
import com.example.apptracker.util.data.getDatabase
import com.example.apptracker.util.navigation.Route
import com.example.apptracker.util.permissions.isPackageUsagePermissionAccessGranted
import com.example.apptracker.util.permissions.tryNotificationPermissionAccess
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

class MainActivity : ComponentActivity() {


    private fun registerNotificationChannel() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            val name = getString(R.string.notification_channel_name)
            val desc = getString(R.string.notification_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(getString(R.string.notification_channel_id), name, importance).apply {
                description = desc
            }
            // register
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun tryPermissions() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
            val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { }
            // notification permission
            tryNotificationPermissionAccess(applicationContext, requestPermissionLauncher)
        }
    }

    @OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        tryPermissions()
        registerNotificationChannel()
        super.onCreate(savedInstanceState)
        setContent {
            val appDatabase = getDatabase(applicationContext)

            val appearanceViewModel = AppearanceViewModel(appDatabase)

            AppTrackerTheme(
                database = appDatabase,
                viewModel = appearanceViewModel
            ) {

                val packageUsagePermissionGranted = isPackageUsagePermissionAccessGranted(applicationContext)

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
                            startDestination = if (packageUsagePermissionGranted) Route.Apps.path else Route.PackageUsagePermission.path,
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
                            composable(Route.PackageUsagePermission.path) {
                                LaunchedEffect(Unit) {
                                    bottomBarState.value = false
                                }
                                PackageUsagePermissionPage(
                                    navController = navController,
                                    context = applicationContext
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

                                /*val builder = NotificationCompat.Builder(applicationContext, getString(R.string.notification_channel_id))
                                    .setSmallIcon(R.mipmap.ic_launcher_round)
                                    .setContentTitle("Settings Opened")
                                    .setContentText("Heehee... It looks like you opened the secret settings menu.")
                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)

                                with(NotificationManagerCompat.from(applicationContext)) {
                                    notify(51251, builder.build())
                                }*/

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