package com.example.apptracker.ui.routes.more.addApps

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.apptracker.R
import com.example.apptracker.ui.components.SearchTopAppBar
import com.example.apptracker.util.data.AppDatabase
import com.example.apptracker.util.navigation.Route
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAppsPage(
    navController: NavController,
    database: AppDatabase,
    viewModel: AddAppsViewModel = AddAppsViewModel(LocalContext.current.packageManager, database)
) {
    val screenState by viewModel.state.collectAsState()
    val trackedApps by screenState.trackedApps.collectAsState(initial = listOf())

    fun search(query: String) {
        viewModel.setQueryString(query)
    }

    fun sort(sortMode: SortFunction) {
        viewModel.setSortMode(sortMode)
    }

    Scaffold(
        topBar = {
            SearchTopAppBar(
                title = { Text(stringResource(id = R.string.more_add_app_button_headline)) },
                onNavigationIconClick = {
                    navController.popBackStack()
                },
                onSearch = { query ->
                    search(query)
                },
                onSortModeChanged = { sortMode ->
                    sort(sortMode)
                }
            )
        }
    ) { padding ->
        val packageManager = LocalContext.current.packageManager
        Column(
            modifier = Modifier
                .padding(top = padding.calculateTopPadding())
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Divider()
            if (screenState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                val sortMode = screenState.queryState.sortMode
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val apps = screenState.apps.filterNot { app -> trackedApps.find { it.packageName == app.packageName } != null }
                    items(apps) { info ->
                        val label = info.loadLabel(packageManager).toString()
                        AddAppListEntry(
                            appInfo = info,
                            packageManager = packageManager,
                            label = label,
                            sortMode = sortMode,
                            onClick = {
                                navController.navigate("${Route.AddApp.argumentlessPath}${info.packageName}") {
                                    launchSingleTop = true
                                    restoreState = true
                                }
                                /*val builder = NotificationCompat.Builder(context, channel)
                                                .setSmallIcon(R.drawable.app_icon)
                                                .setContentTitle(label)
                                                .setContentText(info.packageName)
                                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

                                            with(NotificationManagerCompat.from(context)) {
                                                notify(info.uid, builder.build())
                                            }*/
                            }
                        )
                    }
                }
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAppListEntry(
    appInfo: ApplicationInfo,
    packageManager: PackageManager,
    label: String = appInfo.loadLabel(packageManager).toString(),
    sortMode: SortFunction = SortFunction.Name,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(20.dp)
    ) {
        ListItem(
            modifier = Modifier.fillMaxSize(),
            headlineContent = {
                Text(label)
            },
            supportingContent = {
                when (sortMode) {
                    SortFunction.Size -> {
                        val file = File(appInfo.publicSourceDir)
                        val sizeInMB = file.length() / 1e+6F
                        Text("%.1f MB".format(sizeInMB))
                    }
                    else -> {
                        val packageName = appInfo.packageName
                        if (packageName != label) {
                            Text(packageName)
                        }
                    }
                }
            },
            leadingContent = {
                Image(
                    modifier = Modifier.size(48.dp),
                    painter = rememberDrawablePainter(drawable = appInfo.loadIcon(packageManager)),
                    contentDescription = label,
                    contentScale = ContentScale.FillHeight,
                )
            },
        )
    }
}