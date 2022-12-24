package com.example.apptracker.apps

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

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

    private fun filterApps() {
        isFiltering = true
        val apps = _apps.toList()

        _filteredApps.clear()
        // filter values
        val queryString = _queryState.value.query
        apps.forEach { info ->
            if (queryString == "" || (info.loadLabel(_packageManager).toString()).contains(other = queryString, ignoreCase = true)) {
                _filteredApps.add(info)
            }
        }
        isFiltering = false
    }

    fun setQueryString(value: String) {
        _queryState.update { currentState ->
            currentState.copy(query = value)
        }
        filterApps()
    }

    fun getApps(): List<ApplicationInfo> {
        return _filteredApps.toList()
    }


}