package com.example.apptracker.ui.routes.more.addApps

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apptracker.util.apps.AppsManager
import com.example.apptracker.util.data.AppDatabase
import com.example.apptracker.util.data.apps.TrackedAppDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class AddAppsViewModel(
    private val packageManager: PackageManager,
    database: AppDatabase
) : ViewModel() {

    private val appsManager = AppsManager(packageManager)
    private val trackedAppDao = database.trackedAppDao()

    private var _appsState = MutableStateFlow(AddAppsScreenState())
    val state: StateFlow<AddAppsScreenState> = _appsState.asStateFlow()

    init {
        refreshApps()
    }

    private fun setStateLoading(loading: Boolean = true) {
        _appsState.update { currentState ->
            currentState.copy(
                isLoading = loading
            )
        }
    }

    private fun getPackageInfo(packageName: String): PackageInfo {
        return if (Build.VERSION.SDK_INT >= 33) {
            packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
        } else {
            packageManager.getPackageInfo(packageName, 0)
        }
    }

    private fun refreshApps() = viewModelScope.launch {
        setStateLoading()
        withContext(Dispatchers.IO) {
            _appsState.update {
                it.copy(
                    apps = getFilteredApps(appsManager.getApps()),
                    trackedApps = trackedAppDao.getAll().first()
                )
            }
            setStateLoading(false)
        }
    }

    private fun getFilteredApps(toFilter: MutableList<ApplicationInfo>): List<ApplicationInfo> {
        val filtered = mutableListOf<ApplicationInfo>()
        val queryState = _appsState.value.queryState

        val queryString = queryState.query
        toFilter.forEach { info ->
            if (queryString == "" || (info.loadLabel(packageManager).toString()).contains(
                    other = queryString,
                    ignoreCase = true
                )
            ) {
                filtered.add(info)
            }
        }

        return when (queryState.sortMode) {
            SortFunction.Name -> {
                filtered.sortedBy { it.loadLabel(packageManager).toString() }
            }
            SortFunction.Size -> {
                filtered.sortedByDescending {
                    val file = File(it.publicSourceDir)
                    file.length()
                }
            }
            SortFunction.InstallTime -> {
                filtered.sortedByDescending { getPackageInfo(it.packageName).firstInstallTime }
            }
            SortFunction.LastUpdated -> {
                filtered.sortedByDescending { getPackageInfo(it.packageName).lastUpdateTime }
            }
        }
    }

    fun setQueryString(value: String) {
        _appsState.update { currentState ->
            currentState.copy(
                queryState = currentState.queryState.copy(
                    query = value
                )
            )
        }
        refreshApps()
    }

    fun setSortMode(value: SortFunction) {
        _appsState.update { currentState ->
            currentState.copy(
                queryState = currentState.queryState.copy(
                    sortMode = value
                )
            )
        }
        refreshApps()
    }

}