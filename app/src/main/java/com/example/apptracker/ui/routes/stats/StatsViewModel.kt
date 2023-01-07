package com.example.apptracker.ui.routes.stats

import android.content.pm.PackageManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apptracker.util.apps.AppsManager
import com.example.apptracker.util.apps.TrackedAppsManager
import com.example.apptracker.util.apps.UsageTimeGroupBy
import com.example.apptracker.util.apps.UsageTimeManager
import com.example.apptracker.util.data.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class StatsViewModel(
    database: AppDatabase,
    private val packageManager: PackageManager,
) : ViewModel() {

    private val usageTimeDao = database.usageTimeDao()
    private val trackedAppDao = database.trackedAppDao()
    private val usageTimeManager = UsageTimeManager(database)

    private val _screenState = MutableStateFlow(StatsScreenState())
    val state: StateFlow<StatsScreenState> = _screenState.asStateFlow()

    init {
        refreshUsageTime()
    }

    private fun refreshUsageTime() = viewModelScope.launch {
        val rangeEnd = System.currentTimeMillis()
        val rangeStart = rangeEnd - (1000 * 60 * 60 * 24 * 7)
        withContext(Dispatchers.IO) {
            trackedAppDao.getAll().collectLatest {
                val usageTimes = usageTimeManager.queryUsageTime(rangeStart, rangeEnd)

                _screenState.update {
                    it.copy(
                        usageTime = usageTimes
                    )
                }
            }
        }
    }

    /*private fun refreshApps() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            _screenState.update {
                it.copy(
                    apps = usageTimeDao.getAllWithTimestamp(TrackedAppsManager.getTimestamp()).map { flow ->
                        flow.map { usageInfo ->
                            val appInfo = appsManager.getApp(usageInfo.packageName)
                            StatsScreenApp(
                                usageInfo = usageInfo,
                                appInfo = appInfo,
                                label = appInfo.loadLabel(packageManager).toString(),
                                icon = appInfo.loadIcon(packageManager)
                            )
                        }.sortedByDescending { app -> app.usageInfo.usageTime }
                    }
                )
            }
        }
    }*/

}