package com.example.apptracker.util.apps

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
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

    private val _queryState = MutableStateFlow(AppsViewQueryState())
    val queryState: StateFlow<AppsViewQueryState> = _queryState.asStateFlow()
    private var _appsState = mutableStateOf(AddAppsScreenState())
    val state: State<AddAppsScreenState> = _appsState

    init {
        getApps()
    }

    private fun setStateLoading() {
        _appsState.value = AddAppsScreenState(
            isLoading = true
        )
    }

    private fun getPackageInfo(packageName: String): PackageInfo {
        return if (Build.VERSION.SDK_INT >= 33) {
            _packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
        } else {
            _packageManager.getPackageInfo(packageName, 0)
        }
    }

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
            _queryState.value = AppsViewQueryState()
            _appsState.value = AddAppsScreenState()
            filterApps()
        }
    }

    private fun filterApps() = viewModelScope.launch {
        setStateLoading()
        val apps = _apps.toList()
        val filtered = mutableListOf<ApplicationInfo>()
        val queryString = _queryState.value.query
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
            _appsState.value = AddAppsScreenState(
                apps = when (_queryState.value.sortMode) {
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
    }

    fun setQueryString(value: String) {
        _queryState.update { currentState ->
            currentState.copy(query = value)
        }
        filterApps()
    }

    fun setSortMode(value: SortFunction) {
        _queryState.update { currentState ->
            currentState.copy(sortMode = value)
        }
        filterApps()
    }

}