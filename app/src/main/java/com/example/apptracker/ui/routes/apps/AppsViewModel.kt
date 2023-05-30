package com.example.apptracker.ui.routes.apps

import android.content.pm.PackageManager
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.PagerState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apptracker.util.apps.AppsManager
import com.example.apptracker.util.apps.TrackedAppsManager
import com.example.apptracker.util.data.AppDatabase
import com.example.apptracker.util.data.categories.CategoriesRepository
import com.example.apptracker.util.data.tabs.TabState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAB_STATE_ID = "appsPage"

class AppsViewModel(
    database: AppDatabase,
    private val packageManager: PackageManager,
    private val trackedAppsManager: TrackedAppsManager,
) : ViewModel() {

    private val categoriesRepository = CategoriesRepository(database.categoriesDao())
    private val trackedAppDao = database.trackedAppDao()
    private val tabStateDao = database.tabStateDao()
    private val appsManager = AppsManager(packageManager)

    private val _screenState = MutableStateFlow(AppsScreenState())
    val state: StateFlow<AppsScreenState> = _screenState.asStateFlow()

    init {
        refreshTabState()
        refreshCategories()
        refreshApps()
    }

    private fun refreshTabState() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            _screenState.update {
                it.copy(
                    tabState = tabStateDao.get(TAB_STATE_ID)
                )
            }
        }
    }

    private fun refreshCategories() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            _screenState.update {
                it.copy(
                    categories = categoriesRepository.getCategories(showHidden = true)
                )
            }
        }
    }

    private fun refreshApps() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            _screenState.update {
                it.copy(
                    apps = trackedAppDao.getAll().map { flow ->
                        flow.mapNotNull { trackedApp ->
                            val appInfo = appsManager.getApp(trackedApp.packageName)
                            if (appInfo == null) {
                                null
                            } else {
                                AppsScreenApp(
                                    trackedApp = trackedApp,
                                    appInfo = appInfo,
                                    label = appInfo.loadLabel(packageManager).toString(),
                                    icon = appInfo.loadIcon(packageManager)
                                )
                            }
                        }.sortedBy { app -> app.label }
                    }
                )
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    fun syncPager(pagerState: PagerState) = viewModelScope.launch {
        if (!_screenState.value.pagerSynced) {
            _screenState.update {
                it.copy(
                    pagerSynced = true
                )
            }
            _screenState.value.tabState.firstOrNull()?.let {
                pagerState.scrollToPage(it.page)
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    fun syncTabState(pagerState: PagerState) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val updatedTabState = TabState(
                id = TAB_STATE_ID,
                page = pagerState.currentPage
            )
            tabStateDao.insert(updatedTabState)
        }
    }

    fun setAppOpenedStatus(appInfo: AppsScreenApp, isOpened: Boolean) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            trackedAppsManager.setTrackedAppOpenedStatus(appInfo.trackedApp, isOpened)
        }
    }

    fun updateTrackedAppsOpenedStatus() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            trackedAppsManager.refreshUsageStats()
            trackedAppsManager.updateTrackedAppsOpenedStatus()
        }
    }

}