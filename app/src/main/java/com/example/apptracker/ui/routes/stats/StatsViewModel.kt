package com.example.apptracker.ui.routes.stats

import android.content.pm.PackageManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apptracker.util.apps.AppsManager
import com.example.apptracker.util.apps.UsageTimeManager
import com.example.apptracker.util.data.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.ZonedDateTime

class StatsViewModel(
    database: AppDatabase,
    private val packageManager: PackageManager,
) : ViewModel() {

    private val appsManager = AppsManager(packageManager)
    private val trackedAppDao = database.trackedAppDao()
    private val openLogDao = database.openLogDao()
    private val usageTimeManager = UsageTimeManager(database)

    private val _screenState = MutableStateFlow(StatsScreenState())
    val state: StateFlow<StatsScreenState> = _screenState.asStateFlow()

    init {
        refreshUsageTime()
    }

    private fun refreshUsageTime() = viewModelScope.launch {
        val rangeEnd = System.currentTimeMillis()
        val rangeStart = rangeEnd - ((1000 * 60 * 60 * 24 * 7) + (1000 * 60 * 60 * 5))
        withContext(Dispatchers.IO) {

            /*openLogDao.getOpenedStreaks().collectLatest { openStreaks ->
                print(openStreaks)
            }*/

            trackedAppDao.getAll().collectLatest { trackedApps ->
                val appsInfo: MutableMap<String, StatsScreenAppInfo> = mutableMapOf()

                trackedApps.forEach { trackedApp ->
                    val appInfo = appsManager.getApp(trackedApp.packageName)
                    appsInfo[appInfo.packageName] = StatsScreenAppInfo(
                        appInfo = appInfo,
                        label = appInfo.loadLabel(packageManager).toString(),
                        icon = appInfo.loadIcon(packageManager)
                    )
                }

                _screenState.update {
                    it.copy(
                        usageTime = usageTimeManager.queryUsageTime(
                            rangeStart,
                            rangeEnd,
                            zoneOffset = ZonedDateTime.now().offset
                        ),
                        allTimeUsageTime = usageTimeManager.queryCombinedUsageTime(
                            0,
                            rangeEnd
                        ),
                        appsInfo = appsInfo
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