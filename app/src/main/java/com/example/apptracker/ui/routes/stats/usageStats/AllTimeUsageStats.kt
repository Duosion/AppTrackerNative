package com.example.apptracker.ui.routes.stats.usageStats

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
import com.example.apptracker.ui.routes.stats.UsageTimeBarChart
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllTimeUsageStatsPage(
    navController: NavController,
    viewModel: StatsViewModel
) {
    val screenState by viewModel.state.collectAsState()
    val appsInfo = screenState.appsInfo

    Scaffold (
        topBar = {
            BackTopAppBar(
                title = { ResourceText(id = R.string.stats_all_time_usage_stats_page_name) },
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    ) { padding ->
        screenState.allTimeUsageTime?.let { allTimeUsageTime ->
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
                        ElapsedTimeText(
                            elapsedTime = allTimeUsageTime.combinedUsageTime,
                            style = MaterialTheme.typography.headlineSmall,
                        )
                        ResourceText(
                            id = R.string.stats_all_time_usage_time_label,
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(allTimeUsageTime.values) { usageInfo ->
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
}