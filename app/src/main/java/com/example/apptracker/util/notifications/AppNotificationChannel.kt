package com.example.apptracker.util.notifications

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.apptracker.R
import com.example.apptracker.util.apps.AppsManager
import com.example.apptracker.util.data.apps.TrackedApp
import com.example.apptracker.util.data.apps.TrackedAppReminderOffset
import com.example.apptracker.util.workers.TrackedAppOpenedStatusWorker
import com.example.apptracker.util.workers.TrackedAppReminderWorker
import java.time.*
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit

// val channel = stringResource(id = R.string.notification_channel_id)
class AppNotificationChannel(
    private val channelId: String,
    private val context: Context
) {
    private val workManager: WorkManager = WorkManager.getInstance(context)
    private val packageManager = context.packageManager

    private val appsManager = AppsManager(packageManager)


    fun scheduleTrackedAppReminder(trackedApp: TrackedApp) {
        val useUTC = trackedApp.dayStartIsUTC
        val zoneOffset = if (useUTC) ZoneOffset.UTC else OffsetDateTime.now().offset

        val dateNow = ZonedDateTime.now(zoneOffset).toLocalDateTime()
        val dayStartDate = LocalDateTime.of(LocalDate.now(zoneOffset), LocalTime.of(trackedApp.dayStartHour, trackedApp.dayStartMinute))

        // get reminder time
        val reminderDate = when (TrackedAppReminderOffset.fromId(trackedApp.reminderOffset)) {
            TrackedAppReminderOffset.NO_OFFSET -> {
                dayStartDate
            }
            TrackedAppReminderOffset.FIFTEEN_MINUTES -> {
                dayStartDate.minusMinutes(15)
            }
            TrackedAppReminderOffset.THIRTY_MINUTES -> {
                dayStartDate.minusMinutes(30)
            }
            TrackedAppReminderOffset.ONE_HOUR -> {
                dayStartDate.minusHours(1)
            }
            TrackedAppReminderOffset.CUSTOM -> {
                LocalDateTime.of(LocalDate.now(zoneOffset), LocalTime.of(trackedApp.reminderOffsetHour, trackedApp.reminderOffsetMinute))
            }
        }.let {
            if (dateNow > it) {
                it.plusDays(1)
            } else {
                it
            }
        }

        val secondsDifference = ChronoUnit.SECONDS.between(dateNow, reminderDate)
        val packageName = trackedApp.packageName

        //println("Schedule $packageName reminder for $secondsDifference seconds from now. Day start: $dayStartDate, reminder date: $reminderDate")

        val workerData = Data.Builder()
            .putString("package_name", packageName)
            .putString("notification_channel",channelId)
            .build()

        val reminderWorker = PeriodicWorkRequestBuilder<TrackedAppReminderWorker>(1, TimeUnit.DAYS)
            .addTag(packageName)
            .setInputData(workerData)
            .setInitialDelay(secondsDifference, TimeUnit.SECONDS)
            .build()

        workManager.enqueueUniquePeriodicWork(packageName, ExistingPeriodicWorkPolicy.REPLACE, reminderWorker)
    }

    fun sendTrackedAppReminder(trackedApp: TrackedApp) {
        val packageName = trackedApp.packageName

        val appInfo = appsManager.getApp(packageName)
        val label = appInfo.loadLabel(packageManager)

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.app_icon)
            .setContentTitle(label)
            .setContentText("Day start is approaching for $label.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        packageManager.getLaunchIntentForPackage(packageName)?.let {
            val pending = PendingIntent.getActivity(context, 0, it,PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            builder.setContentIntent(pending)
        }

        with(NotificationManagerCompat.from(context)) {
            notify(appInfo.uid, builder.build())
        }

    }


}