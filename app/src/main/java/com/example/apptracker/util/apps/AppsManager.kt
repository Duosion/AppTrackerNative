package com.example.apptracker.util.apps

import android.annotation.SuppressLint
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build

class AppsManager(
    private val packageManager: PackageManager
) {

    @SuppressLint("QueryPermissionsNeeded")
    fun getApps(): MutableList<ApplicationInfo> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getInstalledApplications(
                PackageManager.ApplicationInfoFlags.of(0)
            )
        } else {
            packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        }
    }

    fun getApp(packageName: String): ApplicationInfo? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.getApplicationInfo(packageName, PackageManager.ApplicationInfoFlags.of(0))
            } else {
                packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
            }
        } catch(exception: PackageManager.NameNotFoundException) {
            null
        }
    }

}