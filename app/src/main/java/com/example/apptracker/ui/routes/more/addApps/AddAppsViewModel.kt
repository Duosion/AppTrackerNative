package com.example.apptracker.ui.routes.more.addApps

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apptracker.util.apps.AppsManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class AddAppsViewModel(
    packageManager: PackageManager
) : ViewModel() {

    private val _packageManager = packageManager
    private val appsManager = AppsManager(packageManager)

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
            _packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
        } else {
            _packageManager.getPackageInfo(packageName, 0)
        }
    }

    private fun refreshApps() = viewModelScope.launch {
        setStateLoading()
        withContext(Dispatchers.IO) {
            _appsState.update {
                it.copy(
                    apps = getFilteredApps(appsManager.getApps())
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
            if (queryString == "" || (info.loadLabel(_packageManager).toString()).contains(
                    other = queryString,
                    ignoreCase = true
                )
            ) {
                filtered.add(info)
            }
        }

        return when (queryState.sortMode) {
            SortFunction.Name -> {
                filtered.sortedBy { it.loadLabel(_packageManager).toString() }
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