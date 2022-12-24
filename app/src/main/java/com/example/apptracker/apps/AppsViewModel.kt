package com.example.apptracker.apps

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel

class AppsViewModel(
    packageManager: PackageManager
) : ViewModel() {

    private val _packageManager = packageManager

    private val _apps = mutableStateListOf<ApplicationInfo>()

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
    }

    fun getApps(): List<ApplicationInfo> {
        return _apps.toList()
    }


}