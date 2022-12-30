package com.example.apptracker.ui.routes.apps

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.apptracker.R
import com.example.apptracker.ui.components.ResourceText
import com.example.apptracker.util.data.AppDatabase
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class, ExperimentalPagerApi::class)
@Composable
fun AppsPage(
    navController: NavController,
    database: AppDatabase,
    viewModel: AppsViewModel = AppsViewModel(database)
) {
    val screenState by viewModel.state.collectAsState()

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
            val pagerState = rememberPagerState()
            val categories = screenState.categories
            val trackedApps = screenState.trackedApps
            val coroutineScope = rememberCoroutineScope()

            val groupedApps = trackedApps.groupBy { trackedApp ->
                val category = categories.find { it.id == trackedApp.categoryId }
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
                count = categories.count(),
                state = pagerState
            ) { page ->
                val category = categories[page]
                val categoryId = category.id
                val items = groupedApps.getOrDefault(categoryId, listOf())

                if (items.isEmpty() && (page == 0)) {
                    // only show empty list text on the first page
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ResourceText(R.string.apps_empty_list_text)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 10.dp, end = 10.dp, top = 10.dp),
                    ) {
                        items(items, key = { it.packageName }) {
                            Card(
                                modifier = Modifier
                                    .padding(bottom = 10.dp)
                                    .height(70.dp)
                                    .fillMaxWidth(),
                                onClick = {

                                }
                            ) {
                                ListItem(
                                    modifier = Modifier.fillMaxSize(),
                                    headlineText = { Text(it.packageName) },
                                    leadingContent = { },
                                    colors = ListItemDefaults.colors(
                                        containerColor = Color(0, 0, 0, 0)
                                    )
                                )
                            }
                        }
                    }
                }
            }

        }
    }
}