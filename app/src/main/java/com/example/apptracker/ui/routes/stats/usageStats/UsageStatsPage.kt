package com.example.apptracker.ui.routes.stats.usageStats

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.apptracker.R
import com.example.apptracker.ui.components.BackTopAppBar
import com.example.apptracker.ui.components.ResourceText
import com.example.apptracker.ui.components.barChart.BarChart
import com.example.apptracker.ui.components.barChart.BarChartBar
import com.example.apptracker.ui.routes.stats.ElapsedTimeText
import com.example.apptracker.ui.routes.stats.StatsViewModel
import com.example.apptracker.ui.routes.stats.UsageStatsListItem
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsageStatsPage(
    navController: NavController,
    viewModel: StatsViewModel,
    defaultSelectedBar: Int
) {
    val screenState by viewModel.state.collectAsState()
    val usageTimes = screenState.usageTime
    val appsInfo = screenState.appsInfo

    var selectedBar by remember { mutableStateOf(defaultSelectedBar) }
    val selectedUsageTime = usageTimes[selectedBar]
    val selectedUsageTimeValues = selectedUsageTime.values

    Scaffold (
        topBar = {
            BackTopAppBar(
                title = { ResourceText(id = R.string.stats_usage_stats_page_name) },
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(top = padding.calculateTopPadding())
                .fillMaxSize()
        ) {

            Surface(
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .fillMaxWidth(),
                tonalElevation = 1.dp,
                shape = MaterialTheme.shapes.large
            ) {
                Column(
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth()
                ) {

                    var largestUsageTime = 0L
                    usageTimes.forEach {
                        largestUsageTime =
                            largestUsageTime.coerceAtLeast(it.combinedUsageTime / 1000)
                    }

                    ElapsedTimeText(
                        elapsedTime = selectedUsageTime.combinedUsageTime,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = stringResource(id = R.string.stats_usage_time_label).format(
                            selectedUsageTime.timestamp.date.format(DateTimeFormatter.ofPattern("LLLL d"))
                        ),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    val labelDateFormatter = DateTimeFormatter.ofPattern("M/d")

                    BarChart(
                        modifier = Modifier
                            .padding(top = 15.dp, bottom = 15.dp)
                            .height(150.dp)
                            .fillMaxWidth(),
                        bars = screenState.usageTime.map {
                            val usageTimeSeconds =
                                TimeUnit.MILLISECONDS.toSeconds(it.combinedUsageTime)
                            BarChartBar(
                                fraction = usageTimeSeconds.toFloat() / largestUsageTime,
                                label = it.timestamp.date.format(labelDateFormatter)
                            )
                        },
                        onSelectedBarChange = {
                            selectedBar = it
                        }
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(selectedUsageTimeValues) { usageInfo ->
                    appsInfo[usageInfo.packageName]?.let { appInfo ->
                        UsageStatsListItem(
                            usageInfo = usageInfo,
                            app = appInfo
                        )
                    }
                }
            }

        }

    }
}