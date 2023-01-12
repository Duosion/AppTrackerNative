package com.example.apptracker.ui.routes.stats

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.apptracker.R
import com.example.apptracker.ui.components.ListItemCard
import com.example.apptracker.ui.components.ResourceText
import com.example.apptracker.ui.components.barChart.BarChart
import com.example.apptracker.ui.components.barChart.BarChartBar
import com.example.apptracker.ui.routes.settings.SettingsListItemCard
import com.example.apptracker.util.apps.GroupedUsageTime
import com.example.apptracker.util.apps.UsageTimeManager
import com.example.apptracker.util.data.apps.TrackedAppUsageTime
import com.example.apptracker.util.navigation.Route
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun StatsPage(
    navController: NavController,
    viewModel: StatsViewModel
) {
    val screenState by viewModel.state.collectAsState()
    val usageTimes = screenState.usageTime
    val appsInfo = screenState.appsInfo

    Scaffold (
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.stats_nav_bar_button_name)) }
            )
        }
    ) { padding ->
        Divider()

        if (appsInfo.isEmpty()) {
            Column(
                modifier = Modifier
                    .padding(top = padding.calculateTopPadding())
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ResourceText(R.string.stats_no_tracked_apps_warning)
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(top = padding.calculateTopPadding())
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Surface(
                    modifier = Modifier
                        .padding(bottom = 10.dp)
                        .fillMaxWidth(),
                    tonalElevation = 1.dp,
                    shape = MaterialTheme.shapes.large
                ){
                    Column(
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth()
                    ) {
                        var selectedBar by remember { mutableStateOf(usageTimes.count() - 1) }
                        val selectedUsageTime = usageTimes[selectedBar]
                        val selectedUsageTimeValues = selectedUsageTime.values

                        UsageTimeBarChart(
                            usageTimes = usageTimes,
                            selectedBar = selectedBar,
                            onSelectedBarChanged = {
                                selectedBar = it
                            }
                        )

                        val itemCount = selectedUsageTimeValues.count()
                        val topThree = selectedUsageTimeValues.subList(0, itemCount.coerceAtMost(3))

                        if (topThree.isNotEmpty()) {
                            Divider(
                                modifier = Modifier.padding(bottom = 5.dp)
                            )

                            topThree.forEach { usageInfo ->
                                appsInfo[usageInfo.packageName]?.let { appInfo ->
                                    UsageStatsListItem(
                                        usageInfo = usageInfo,
                                        app = appInfo
                                    )
                                }
                            }

                            if (itemCount > 3) {
                                Divider(
                                    modifier = Modifier.padding(vertical = 5.dp)
                                )
                                TextButton(
                                    modifier = Modifier.fillMaxWidth(),
                                    onClick = {
                                        // go to usage stats page
                                        navController.navigate(Route.UsageStats.argumentlessPath + selectedBar) {
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                ) {
                                    ResourceText(R.string.stats_usage_time_see_all)
                                }
                            }
                        }
                    }
                }

                screenState.allTimeUsageTime?.let { allTimeUsageTime ->
                    ListItemCard(
                        modifier = Modifier
                            .padding(bottom = 10.dp)
                            .fillMaxWidth(),
                        tonalElevation = 1.dp,
                        headlineText = {
                            ElapsedTimeText(elapsedTime = allTimeUsageTime.combinedUsageTime)
                        },
                        supportingText = {
                            ResourceText(id = R.string.stats_all_time_usage_time_label)
                        },
                        trailingContent = {
                            val values = allTimeUsageTime.values
                            val topTwo = values.subList(0, values.count().coerceAtMost(2))

                            Row {
                                topTwo.forEach {
                                    appsInfo[it.packageName]?.let { appInfo ->
                                        Image(
                                            modifier = Modifier
                                                .padding(end = 5.dp)
                                                .size(30.dp),
                                            painter = rememberDrawablePainter(drawable = appInfo.icon),
                                            contentDescription = appInfo.label,
                                            contentScale = ContentScale.FillHeight,
                                        )
                                    }
                                }
                            }
                        },
                        onClick = {
                            navController.navigate(Route.AllTimeUsageStats.path) {
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        listItemColors = ListItemDefaults.colors(
                            containerColor = Color.Transparent
                        )
                    )
                }

            }

        }
    }
}

@Composable
fun UsageTimeBarChart(
    usageTimes: List<GroupedUsageTime>,
    selectedBar: Int = usageTimes.count() - 1,
    onSelectedBarChanged: (Int) -> Unit
) {
    val selectedUsageTime = usageTimes[selectedBar]

    var largestUsageTime = 1L
    usageTimes.forEach {
        largestUsageTime = largestUsageTime.coerceAtLeast(it.combinedUsageTime / 1000)
    }

    ElapsedTimeText(
        elapsedTime = selectedUsageTime.combinedUsageTime,
        style = MaterialTheme.typography.headlineSmall,
        color = MaterialTheme.colorScheme.onSurface
    )
    Text(
        text = stringResource(id = R.string.stats_usage_time_label).format(selectedUsageTime.timestamp.date.format(DateTimeFormatter.ofPattern("LLLL d"))),
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.onSurface
    )

    val labelDateFormatter = DateTimeFormatter.ofPattern("M/d")

    BarChart(
        modifier = Modifier
            .padding(top = 15.dp, bottom = 15.dp)
            .height(150.dp)
            .fillMaxWidth(),
        bars = usageTimes.map {
            val usageTimeSeconds = TimeUnit.MILLISECONDS.toSeconds(it.combinedUsageTime)
            BarChartBar(
                fraction = usageTimeSeconds.toFloat() / largestUsageTime,
                label = it.timestamp.date.format(labelDateFormatter)
            )
        },
        selectedBar = selectedBar,
        onSelectedBarChange = onSelectedBarChanged
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsageStatsListItem(
    usageInfo: TrackedAppUsageTime,
    app: StatsScreenAppInfo
) {
    val label = app.label
    ListItem(
        modifier = Modifier.fillMaxSize(),
        headlineText = {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(0.5f),
                    text = label!!,
                    textAlign = TextAlign.Start
                )
                ElapsedTimeText(
                    modifier = Modifier.fillMaxWidth(),
                    elapsedTime = usageInfo.usageTime,
                    textAlign = TextAlign.End
                )
            }

        },
        leadingContent = {
            Image(
                modifier = Modifier.size(36.dp),
                painter = rememberDrawablePainter(drawable = app.icon),
                contentDescription = label,
                contentScale = ContentScale.FillHeight,
            )
        },
        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent
        )
    )
}

@Composable
fun ElapsedTimeText(
    // the elapsed time in milliseconds
    modifier: Modifier = Modifier,
    elapsedTime: Long,
    textAlign: TextAlign? = null,
    style: TextStyle = LocalTextStyle.current,
    color: Color = Color.Unspecified
) {
    val hours = TimeUnit.MILLISECONDS.toHours(elapsedTime)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(elapsedTime) - TimeUnit.HOURS.toMinutes(hours)

    val formatted = when {
        (hours > 0) -> {
            String.format(stringResource(id = R.string.stats_elapsed_time_hour_and_minute_format_string),
                hours,
                minutes
            )
        }
        (minutes > 0) -> {
            String.format(stringResource(id = R.string.stats_elapsed_time_minute_format_string),
                minutes
            )
        }
        else -> {
            String.format(stringResource(id = R.string.stats_elapsed_time_seconds_format_string),
                TimeUnit.MILLISECONDS.toSeconds(elapsedTime) - TimeUnit.MINUTES.toSeconds(minutes)
            )
        }
    }
    
    Text(
        modifier = modifier,
        text = formatted,
        textAlign = textAlign,
        style = style,
        color = color
    )
}