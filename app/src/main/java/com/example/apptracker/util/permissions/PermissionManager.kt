package com.example.apptracker.util.permissions

import android.app.AppOpsManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat

/*@RequiresApi(Build.VERSION_CODES.M)
fun tryNotificationPolicyAccess(
    notificationManager: NotificationManager,
    context: Context
): Boolean {
    return if (notificationManager.isNotificationPolicyAccessGranted) {
        true
    } else {
        val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
        startActivity(context, intent, null)
        false
    }
}*/

fun tryNotificationPermissionAccess(
    context: Context,
    requestPermissionLauncher: ActivityResultLauncher<String>
) {
    when {
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU -> {}
        ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED -> {}
        else -> {
            requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}

/*
AppOpsManager appOps = (AppOpsManager) context
        .getSystemService(Context.APP_OPS_SERVICE);
int mode = appOps.checkOpNoThrow("android:get_usage_stats",
        android.os.Process.myUid(), context.getPackageName());
boolean granted = mode == AppOpsManager.MODE_ALLOWED;
*/
fun isPackageUsagePermissionAccessGranted(
    context: Context
): Boolean {
    val opsManager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
    val mode = when {
        Build.VERSION.SDK_INT > Build.VERSION_CODES.Q -> opsManager.unsafeCheckOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), context.packageName)
        else -> opsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), context.packageName)
    }

    return mode == AppOpsManager.MODE_ALLOWED
}
