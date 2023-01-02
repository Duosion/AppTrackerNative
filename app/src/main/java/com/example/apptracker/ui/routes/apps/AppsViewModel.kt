package com.example.apptracker.ui.routes.apps

import android.content.Context
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apptracker.util.apps.AppsManager
import com.example.apptracker.util.apps.TrackedAppsManager
import com.example.apptracker.util.data.AppDatabase
import com.example.apptracker.util.data.categories.CategoriesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AppsViewModel(
    private val database: AppDatabase,
    private val packageManager: PackageManager,
    private val trackedAppsManager: TrackedAppsManager
) : ViewModel() {

    private val categoriesRepository = CategoriesRepository(database.categoriesDao())
    private val trackedAppDao = database.trackedAppDao()
    private val appsManager = AppsManager(packageManager)

    private val _screenState = MutableStateFlow(AppsScreenState())
    val state: StateFlow<AppsScreenState> = _screenState.asStateFlow()

    init {
        refreshCategories()
        refreshApps()
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
                        flow.map { trackedApp ->
                            val appInfo = appsManager.getApp(trackedApp.packageName)
                            AppsScreenApp(
                                trackedApp = trackedApp,
                                appInfo = appInfo,
                                label = appInfo.loadLabel(packageManager).toString(),
                                icon = appInfo.loadIcon(packageManager)
                            )
                        }.sortedBy { app -> app.label }
                    }
                )
            }
        }
    }

    fun setAppOpenedStatus(appInfo: AppsScreenApp, isOpened: Boolean) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            trackedAppsManager.setTrackedAppOpenedStatus(appInfo.trackedApp, isOpened)
        }
    }

}