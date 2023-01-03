package com.example.apptracker.util.workers

import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.apptracker.R
import com.example.apptracker.util.apps.TrackedAppsManager
import com.example.apptracker.util.data.getDatabase
import com.example.apptracker.util.notifications.AppNotificationChannel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext

class TrackedAppReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        try {
            // import params
            val packageName = inputData.getString("package_name") ?: return Result.failure()
            val notificationChannel = inputData.getString("notification_channel") ?: return Result.failure()
            // ensure they exist

            val database = getDatabase(applicationContext)
            val trackedAppDao = database.trackedAppDao()

            val app = trackedAppDao.get(packageName)

            app.firstOrNull()?.let {
                if (!it.openedToday) {
                    AppNotificationChannel(
                        channelId = notificationChannel,
                        context = applicationContext
                    ).sendTrackedAppReminder(
                        trackedApp = it
                    )
                }
            }

        } catch (error: java.lang.Exception) {
            return Result.failure()
        }
        return Result.success()
    }

}