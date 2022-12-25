package com.example.apptracker.util.apps

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class AppsViewModel(
    packageManager: PackageManager
) : ViewModel() {

    private val _packageManager = packageManager
    private val _apps = mutableStateListOf<ApplicationInfo>()
    private val _filteredApps = mutableStateListOf<ApplicationInfo>()
    private val _queryState = MutableStateFlow(AppsViewQueryState())
    val queryState: StateFlow<AppsViewQueryState> = _queryState.asStateFlow()

    var isFiltering by mutableStateOf(false)

    init {
        // get apps
        refreshApps()
    }

    private fun refreshApps() {
        _apps.clear()
        // add new apps
        if (Build.VERSION.SDK_INT >= 33) {
            _apps.addAll(_packageManager.getInstalledApplications(PackageManager.ApplicationInfoFlags.of(0)))
        } else {
            _apps.addAll(_packageManager.getInstalledApplications(PackageManager.GET_META_DATA))
        }
        _queryState.value = AppsViewQueryState()
        filterApps()
    }

    private fun getPackageInfo(packageName: String): PackageInfo {
        return if (Build.VERSION.SDK_INT >= 33) {
            _packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
        } else {
            _packageManager.getPackageInfo(packageName, 0)
        }
    }

    private fun filterApps() {
        isFiltering = true
        val apps = _apps.toList()
        //Thread.sleep(1000)
        _filteredApps.clear()
        // filter values
        val filtered = mutableListOf<ApplicationInfo>()
        val queryString = _queryState.value.query
        apps.forEach { info ->
            if (queryString == "" || (info.loadLabel(_packageManager).toString()).contains(other = queryString, ignoreCase = true)) {
                filtered.add(info)
            }
        }
        // sort
        _filteredApps.addAll( when(_queryState.value.sortMode) {
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
        })
        isFiltering = false
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

    fun getSortMode(): SortFunction {
        return _queryState.value.sortMode
    }

    fun getApps(): List<ApplicationInfo> {
        return _filteredApps.toList()
    }


}