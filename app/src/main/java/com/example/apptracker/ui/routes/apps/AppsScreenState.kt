package com.example.apptracker.ui.routes.apps

import com.example.apptracker.util.data.categories.Category
import com.example.apptracker.util.data.tabs.TabState
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

data class AppsScreenState (
    var apps: Flow<List<AppsScreenApp>> = MutableStateFlow(listOf()),
    val categories: Flow<List<Category>> = MutableStateFlow(listOf()),
    val tabState: Flow<TabState?> = MutableStateFlow(null),
    val pagerSynced: Boolean = false,
    val isLoading: Boolean = false
)
