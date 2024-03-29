package com.example.apptracker.ui.routes.apps

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.example.apptracker.R
import com.example.apptracker.ui.components.ResourceText
import com.example.apptracker.ui.components.TrackedAppLastOpenedText
import com.example.apptracker.ui.routes.settings.SettingsListItemCard
import com.example.apptracker.util.navigation.Route
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AppsPage(
    navController: NavController,
    context: Context = LocalContext.current,
    viewModel: AppsViewModel
) {
    val screenState by viewModel.state.collectAsState()

    val categories by screenState.categories.collectAsState(initial = listOf())
    
    val coroutineScope = rememberCoroutineScope()

    var bottomSheetOpen by remember { mutableStateOf(AppsInfoSheetState()) }
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false
    )

    fun hideBottomSheet() {
        coroutineScope.launch { bottomSheetState.hide() }.invokeOnCompletion {
            if (!bottomSheetState.isVisible) {
                bottomSheetOpen = AppsInfoSheetState()
            }
        }
    }

    val pagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f
    ) {
        categories.count()
    }
    viewModel.syncPager(pagerState)

    val lifeCycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifeCycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.updateTrackedAppsOpenedStatus()
            }
        }
        lifeCycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifeCycleOwner.lifecycle.removeObserver(observer)
        }
    }

    when {
        bottomSheetOpen.enabled && bottomSheetOpen.app != null -> {
            bottomSheetOpen.app?.let { app ->
                AppInfoBottomSheet(
                    onDismissRequest = { bottomSheetOpen = AppsInfoSheetState() },
                    sheetState = bottomSheetState,
                    app = app,
                    onOpenClick = {
                        val intent =
                            context.packageManager.getLaunchIntentForPackage(app.trackedApp.packageName)
                        if (intent != null) {
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            ContextCompat.startActivity(context, intent, null)
                        }
                        // close sheet
                        hideBottomSheet()
                    },
                    onSettingsClick = {
                        navController.navigate("${Route.EditApp.argumentlessPath}${app.trackedApp.packageName}") {
                            launchSingleTop = true
                            restoreState = true
                        }
                        // close sheet
                        hideBottomSheet()
                    }
                )
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.apps_nav_bar_button_name)) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(top = padding.calculateTopPadding())
                .fillMaxSize()
        ) {
            val apps by screenState.apps.collectAsState(initial = listOf())

            val groupedApps = apps.groupBy { app ->
                val category = categories.find { it.id == app.trackedApp.categoryId }
                category?.id ?: 1
            }

            if (categories.count() > 1) {
                ScrollableTabRow(
                    modifier = Modifier.fillMaxWidth(),
                    selectedTabIndex = pagerState.currentPage,
                    edgePadding = 4.dp,
                    indicator = {
                        Box(
                            modifier = Modifier
                                .tabIndicatorOffset(it[pagerState.currentPage])
                                .height(4.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                                )
                        )
                    },
                    divider = {}
                ) {
                    categories.forEachIndexed { index, category ->
                        Tab(
                            text = { Text(category.name) },
                            selected = pagerState.currentPage == index,
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            },
                            selectedContentColor = MaterialTheme.colorScheme.primary,
                            unselectedContentColor = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
                Divider()
            }
            HorizontalPager(
                state = pagerState
            ) { page ->
                val category = categories[page]
                val categoryId = category.id
                val items = groupedApps.getOrDefault(categoryId, listOf())

                viewModel.syncTabState(pagerState)

                if (items.isEmpty() && (page == 0)) {
                    // only show empty list text on the first page
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ResourceText(R.string.apps_empty_list_text)
                        Button(
                            modifier = Modifier.padding(top = 10.dp),
                            onClick = {
                                navController.navigate(Route.AddApps.path) {
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        ) {
                            ResourceText(R.string.apps_add_app_title)
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        items(items, key = { it.trackedApp.packageName }) {
                            AppCard(
                                app = it,
                                onClick = {
                                    bottomSheetOpen = AppsInfoSheetState(
                                        enabled = true,
                                        app = it
                                    )
                                },
                                onCheckedChange = { state ->
                                    viewModel.setAppOpenedStatus(it, state)
                                }
                            )
                        }
                    }
                }
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppCard(
    app: AppsScreenApp,
    onClick: () -> Unit,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .padding(bottom = 10.dp)
            .height(70.dp)
            .fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        AppListItem(
            app = app,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
fun AppListItem(
    app: AppsScreenApp,
    onCheckedChange: (Boolean) -> Unit
) {
    val label = app.label
    ListItem(
        modifier = Modifier.fillMaxSize(),
        headlineContent = { Text(label!!) },
        leadingContent = {
            if (app.painter == null) {
                app.painter = rememberDrawablePainter(drawable = app.icon)
            }
            Image(
                modifier = Modifier.size(48.dp),
                painter = app.painter!!,
                contentDescription = label,
                contentScale = ContentScale.FillHeight,
            )
        },
        supportingContent = {
            TrackedAppLastOpenedText(app.trackedApp)
        },
        trailingContent = {
            Checkbox(
                checked = app.trackedApp.openedToday,
                onCheckedChange = onCheckedChange
            )
        },
        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppInfoBottomSheet(
    onDismissRequest: () -> Unit,
    sheetState: SheetState,
    app: AppsScreenApp,
    onOpenClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        tonalElevation = 3.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val label = app.label
            Image(
                modifier = Modifier.size(60.dp),
                painter = app.painter ?: rememberDrawablePainter(drawable = app.icon),
                contentDescription = label,
                contentScale = ContentScale.FillHeight,
            )
            Spacer(modifier = Modifier.padding(bottom = 5.dp))
            Text(
                text = label!!,
                style = MaterialTheme.typography.titleLarge
            )
            TrackedAppLastOpenedText(
                trackedApp = app.trackedApp,
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.padding(bottom = 10.dp))
            AppInfoDialogCard(
                icon = R.drawable.open_icon,
                text = R.string.apps_info_dialog_open_app_headline,
                onClick = onOpenClick
            )
            AppInfoDialogCard(
                icon = R.drawable.settings_icon,
                text = R.string.apps_info_dialog_settings_headline,
                onClick = onSettingsClick
            )
        }
    }
}

@Composable
fun AppInfoDialogCard(
    icon: Int,
    text: Int,
    onClick: () -> Unit
) {
    SettingsListItemCard(
        leadingContent = {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = stringResource(id = text)
            )
        },
        headlineContent = {
            ResourceText(text)
        },
        onClick = onClick
    )
}

