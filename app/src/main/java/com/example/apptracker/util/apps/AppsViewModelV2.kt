package com.example.apptracker.util.apps

import android.annotation.SuppressLint
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class AppsViewModelV2(
    packageManager: PackageManager
) : ViewModel() {

    private val _packageManager = packageManager
    private val _apps = mutableStateListOf<ApplicationInfo>()

    private var _appsState = MutableStateFlow(AddAppsScreenState())
    val state: StateFlow<AddAppsScreenState> = _appsState.asStateFlow()

    init {
        getApps()
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

    @SuppressLint("QueryPermissionsNeeded")
    private fun getApps() = viewModelScope.launch {
        setStateLoading()
        withContext(Dispatchers.IO) {
            // add new apps
            if (Build.VERSION.SDK_INT >= 33) {
                _apps.addAll(
                    _packageManager.getInstalledApplications(
                        PackageManager.ApplicationInfoFlags.of(
                            0
                        )
                    )
                )
            } else {
                _apps.addAll(_packageManager.getInstalledApplications(PackageManager.GET_META_DATA))
            }
            _appsState.value = AddAppsScreenState()
            filterApps()
        }
    }

    private fun filterApps() = viewModelScope.launch {
        setStateLoading()
        val apps = _apps.toList()
        val filtered = mutableListOf<ApplicationInfo>()
        val queryState = _appsState.value.queryState

        val queryString = queryState.query
        withContext(Dispatchers.IO) {
            apps.forEach { info ->
                if (queryString == "" || (info.loadLabel(_packageManager).toString()).contains(
                        other = queryString,
                        ignoreCase = true
                    )
                ) {
                    filtered.add(info)
                }
            }
            // sort
            _appsState.update { currentState ->
                currentState.copy(
                    apps = when (queryState.sortMode) {
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
                )
            }
            setStateLoading(false)
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
        filterApps()
    }

    fun setSortMode(value: SortFunction) {
        _appsState.update { currentState ->
            currentState.copy(
                queryState = currentState.queryState.copy(
                    sortMode = value
                )
            )
        }
        filterApps()
    }

}