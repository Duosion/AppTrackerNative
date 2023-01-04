package com.example.apptracker.util.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.apptracker.util.data.getDatabase
import com.example.apptracker.util.notifications.AppNotificationChannel
import kotlinx.coroutines.flow.firstOrNull

class TrackedAppReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        try {
            // import params
            val packageName = inputData.getString("package_name") ?: return Result.failure()
            // ensure they exist

            val database = getDatabase(applicationContext)
            val trackedAppDao = database.trackedAppDao()

            val app = trackedAppDao.get(packageName)

            app.firstOrNull()?.let {
                if (!it.openedToday) {
                    AppNotificationChannel(
                        context = applicationContext
                    ).scheduleTrackedAppReminder(it)
                }
            }

        } catch (error: java.lang.Exception) {
            return Result.failure()
        }
        return Result.success()
    }

}