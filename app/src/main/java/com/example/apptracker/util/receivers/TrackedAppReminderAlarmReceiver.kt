package com.example.apptracker.util.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.*
import com.example.apptracker.util.data.getDatabase
import com.example.apptracker.util.notifications.AppNotificationChannel
import com.example.apptracker.util.workers.TrackedAppReminderWorker
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.firstOrNull
import java.util.concurrent.TimeUnit

class TrackedAppReminderAlarmReceiver : BroadcastReceiver() {

    private val coroutineScope = CoroutineScope(SupervisorJob())

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "reminder_alarm") {
            val pendingResult = goAsync()

            // import params
            val packageName = intent.getStringExtra("package_name") ?: return

            // ensure they exist

            val database = getDatabase(context)
            val trackedAppDao = database.trackedAppDao()

            val app = trackedAppDao.get(packageName)

            coroutineScope.launch(Dispatchers.IO) {
                try {
                    app.firstOrNull()?.let {
                        if (!it.openedToday) {
                            AppNotificationChannel(
                                context = context
                            ).sendTrackedAppReminder(
                                trackedApp = it
                            )
                            // schedule next reminder
                            val workerData = Data.Builder()
                                .putString("package_name", packageName)
                                .build()

                            val reminderWorker = OneTimeWorkRequestBuilder<TrackedAppReminderWorker>()
                                .addTag(packageName)
                                .setInputData(workerData)
                                .setInitialDelay(60, TimeUnit.SECONDS)
                                .build()

                            WorkManager.getInstance(context).enqueueUniqueWork(packageName, ExistingWorkPolicy.REPLACE, reminderWorker)
                        }
                    }
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}