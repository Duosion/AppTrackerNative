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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun UsageStatsPage(
    navController: NavController,
    viewModel: StatsViewModel,
    defaultSelectedBar: Int
) {
    val screenState by viewModel.state.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(
        initialPage = defaultSelectedBar
    )

    val usageTimes = screenState.usageTime
    val appsInfo = screenState.appsInfo

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
                    UsageTimeBarChart(
                        usageTimes = usageTimes,
                        selectedBar = pagerState.currentPage,
                        onSelectedBarChanged = { newPage ->
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(newPage)
                            }
                        }
                    )
                }
            }

            HorizontalPager(
                pageCount = usageTimes.count(),
                state = pagerState
            ) { page ->
                val selectedUsageTime = usageTimes[page]
                val selectedUsageTimeValues = selectedUsageTime.values

                if (selectedUsageTimeValues.isEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(id = R.string.stats_usage_stats_no_data_warning).format(selectedUsageTime.timestamp.date.format(DateTimeFormatter.ofPattern(
                                "LLLL d"))),
                        )
                    }
                } else {
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

    }
}