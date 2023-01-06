package com.example.apptracker.ui.routes.stats

import android.content.pm.PackageManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apptracker.util.apps.AppsManager
import com.example.apptracker.util.apps.TrackedAppsManager
import com.example.apptracker.util.data.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StatsViewModel(
    database: AppDatabase,
    private val packageManager: PackageManager,
) : ViewModel() {

    private val usageTimeDao = database.usageTimeDao()
    private val appsManager = AppsManager(packageManager)

    private val _screenState = MutableStateFlow(StatsScreenState())
    val state: StateFlow<StatsScreenState> = _screenState.asStateFlow()

    init {
        refreshApps()
    }

    private fun refreshApps() = viewModelScope.launch {
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
    }

}