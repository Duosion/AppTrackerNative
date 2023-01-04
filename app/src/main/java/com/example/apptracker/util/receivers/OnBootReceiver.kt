package com.example.apptracker.util.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.apptracker.util.data.getDatabase
import com.example.apptracker.util.notifications.AppNotificationChannel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class OnBootReceiver : BroadcastReceiver() {

    private val coroutineScope = CoroutineScope(SupervisorJob())

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            val pendingResult = goAsync()

            val database = getDatabase(context)
            val trackedAppDao = database.trackedAppDao()

            val apps = trackedAppDao.getAll()

            val channel = AppNotificationChannel(
                context = context
            )

            coroutineScope.launch(Dispatchers.IO) {
                try {
                    apps.firstOrNull()?.let {
                        channel.scheduleTrackedAppsReminders(it)
                    }
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}