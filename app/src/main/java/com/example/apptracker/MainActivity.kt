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
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.apptracker.ui.routes.PermissionPage
import com.example.apptracker.ui.routes.apps.AppsPage
import com.example.apptracker.ui.routes.apps.AppsViewModel
import com.example.apptracker.ui.routes.apps.addApp.AddAppPage
import com.example.apptracker.ui.routes.apps.addApp.AddAppViewModel
import com.example.apptracker.ui.routes.apps.addApp.AppPageMode
import com.example.apptracker.ui.routes.apps.addApp.EditAppViewModel
import com.example.apptracker.ui.routes.more.MorePage
import com.example.apptracker.ui.routes.more.addApps.AddAppsPage
import com.example.apptracker.ui.routes.more.addApps.AddAppsViewModel
import com.example.apptracker.ui.routes.more.categories.CategoriesPage
import com.example.apptracker.ui.routes.more.categories.CategoriesViewModel
import com.example.apptracker.ui.routes.settings.SettingsPage
import com.example.apptracker.ui.routes.settings.appearance.AppearancePage
import com.example.apptracker.ui.routes.settings.appearance.AppearanceViewModel
import com.example.apptracker.ui.routes.settings.general.GeneralPage
import com.example.apptracker.ui.theme.AppTrackerTheme
import com.example.apptracker.util.apps.AppsManager
import com.example.apptracker.util.apps.TrackedAppsManager
import com.example.apptracker.util.data.getDatabase
import com.example.apptracker.util.navigation.MainNavItem
import com.example.apptracker.util.navigation.Route
import com.example.apptracker.util.notifications.AppNotificationChannel
import com.example.apptracker.util.permissions.isPackageUsagePermissionAccessGranted
import com.example.apptracker.util.permissions.tryNotificationPermissionAccess
import com.example.apptracker.util.workers.TrackedAppOpenedStatusWorker
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.firstOrNull
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    private fun registerNotificationChannel() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            val name = getString(R.string.notification_channel_name)
            val desc = getString(R.string.notification_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(
                getString(R.string.notification_channel_id),
                name,
                importance
            ).apply {
                description = desc
            }
            // register
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun tryPermissions() {
        val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { }
        // notification permission
        tryNotificationPermissionAccess(applicationContext, requestPermissionLauncher)
    }

    private fun enqueueWork() {
        val openedStatusWorkerTag = getString(R.string.apps_opened_status_worker_tag)
        val openedStatusWorker = PeriodicWorkRequestBuilder<TrackedAppOpenedStatusWorker>(1, TimeUnit.HOURS)
            .addTag(openedStatusWorkerTag)
            .build()

        val workManager = WorkManager.getInstance(applicationContext)
        workManager.enqueueUniquePeriodicWork(openedStatusWorkerTag, ExistingPeriodicWorkPolicy.KEEP, openedStatusWorker)
    }

    @OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        tryPermissions()
        registerNotificationChannel()

        // register workers
        enqueueWork()

        super.onCreate(savedInstanceState)
        setContent {
            val appDatabase = getDatabase(applicationContext)

            val context = LocalContext.current

            val packageManager = context.packageManager

            val trackedAppsManager = TrackedAppsManager(appDatabase, context)

            val notificationChannel = AppNotificationChannel(
                applicationContext
            )

            val appearanceViewModel = AppearanceViewModel(appDatabase)
            val addAppsViewModel = AddAppsViewModel(packageManager, appDatabase)
            val appsViewModel = AppsViewModel(
                database = appDatabase,
                packageManager = packageManager,
                trackedAppsManager = trackedAppsManager,
            )
            val categoriesViewModel = CategoriesViewModel(appDatabase)

            // update opened status
            LaunchedEffect(context) {
                withContext(Dispatchers.IO) {
                    trackedAppsManager.updateTrackedAppsOpenedStatus()
                    // schedule reminders
                    appDatabase.trackedAppDao().getAll().firstOrNull()?.let {
                        notificationChannel.scheduleTrackedAppsReminders(it)
                    }
                }
            }

            AppTrackerTheme(
                database = appDatabase,
                viewModel = appearanceViewModel
            ) {

                val packageUsagePermissionGranted = isPackageUsagePermissionAccessGranted(applicationContext)

                val transitionTweenTime = 350 // the transition tween time in ms
                val fadeInTransition =
                    fadeIn(animationSpec = tween(durationMillis = transitionTweenTime))
                val fadeOutTransition =
                    fadeOut(animationSpec = tween(durationMillis = transitionTweenTime))

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
                                AppsPage(
                                    navController = navController,
                                    viewModel = appsViewModel
                                )
                            }
                            composable(Route.AddApp.path) { backStackEntry ->
                                LaunchedEffect(Unit) {
                                    bottomBarState.value = false
                                }

                                val packageName: String? =
                                    backStackEntry.arguments?.getString("packageName")
                                val appsManager = AppsManager(packageManager)

                                if (packageName == null) {
                                    navController.popBackStack()
                                } else {
                                    val appInfo = appsManager.getApp(packageName)
                                    AddAppPage(
                                        navController = navController,
                                        appInfo = appInfo,
                                        mode = AppPageMode.ADD,
                                        viewModel = AddAppViewModel(appDatabase, packageName, notificationChannel),
                                    )
                                }
                            }
                            composable(Route.EditApp.path) { backStackEntry ->
                                LaunchedEffect(Unit) {
                                    bottomBarState.value = false
                                }

                                val packageName: String? =
                                    backStackEntry.arguments?.getString("packageName")
                                val appsManager = AppsManager(packageManager)

                                if (packageName == null) {
                                    navController.popBackStack()
                                } else {
                                    val appInfo = appsManager.getApp(packageName)
                                    AddAppPage(
                                        navController = navController,
                                        appInfo = appInfo,
                                        mode = AppPageMode.EDIT,
                                        viewModel = EditAppViewModel(appDatabase, packageName, notificationChannel)
                                    )
                                }
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
                                PermissionPage(
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
                                    navController = navController,
                                    database = appDatabase,
                                    viewModel = addAppsViewModel
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
                                    database = appDatabase,
                                    viewModel = categoriesViewModel
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
                            composable(Route.General.path) {
                                LaunchedEffect(Unit) {
                                    bottomBarState.value = false
                                }
                                GeneralPage(
                                    navController = navController
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
                    icon = {
                        Icon(
                            painter = painterResource(id = navItem.icon),
                            contentDescription = itemName
                        )
                    },
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