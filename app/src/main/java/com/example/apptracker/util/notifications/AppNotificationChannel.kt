package com.example.apptracker.util.notifications

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.apptracker.R
import com.example.apptracker.util.apps.AppsManager
import com.example.apptracker.util.data.apps.TrackedApp
import com.example.apptracker.util.data.apps.TrackedAppReminderOffset
import com.example.apptracker.util.receivers.TrackedAppReminderAlarmReceiver
import java.time.*
import java.time.temporal.ChronoUnit
import android.app.PendingIntent
import android.os.Build


class AppNotificationChannel(
    private val context: Context
) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    //private val workManager: WorkManager = WorkManager.getInstance(context)
    private val packageManager = context.packageManager

    private val channelId = context.getString(R.string.notification_channel_id)

    private val appsManager = AppsManager(packageManager)

    private val canScheduleExactAlarms = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            alarmManager.canScheduleExactAlarms()
        }
        else -> true
    }

    private fun getPendingReminderIntent(trackedApp: TrackedApp): PendingIntent {
        val alarmIntent = Intent(context, TrackedAppReminderAlarmReceiver::class.java)
        alarmIntent.action = "reminder_alarm"
        alarmIntent.putExtra("package_name",trackedApp.packageName)

        return PendingIntent.getBroadcast(context, trackedApp.uid, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }

    fun scheduleTrackedAppReminder(trackedApp: TrackedApp) {
        // ensure that the reminder is enabled in the first place
        if (!trackedApp.reminderNotification) { return }

        val useUTC = trackedApp.dayStartIsUTC
        val zoneOffset = if (useUTC) ZoneOffset.UTC else OffsetDateTime.now().offset

        val dateNow = ZonedDateTime.now(zoneOffset).toLocalDateTime()
        var dayStartDate = LocalDateTime.of(LocalDate.now(zoneOffset), LocalTime.of(trackedApp.dayStartHour, trackedApp.dayStartMinute)).let {
            if (dateNow > it) {
                it.plusDays(1)
            } else {
                it
            }
        }

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
                dayStartDate = dayStartDate.plusDays(1)
                it.plusDays(1)
            } else {
                it
            }
        }

        val secondsDifference = ChronoUnit.SECONDS.between(dateNow, reminderDate)

        val pendingAlarmIntent = getPendingReminderIntent(trackedApp)

        alarmManager.cancel(pendingAlarmIntent)
        if (canScheduleExactAlarms) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + secondsDifference * 1000,
                pendingAlarmIntent
            )
        } else {
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + secondsDifference * 1000,
                pendingAlarmIntent
            )
        }

        println("Schedule ${trackedApp.packageName} reminder for $secondsDifference seconds from now. Day start: $dayStartDate, reminder date: $reminderDate")

        /*val workerData = Data.Builder()
            .putString("package_name", packageName)
            .putString("notification_channel",channelId)
            .build()

        val reminderWorker = PeriodicWorkRequestBuilder<TrackedAppReminderWorker>(1, TimeUnit.DAYS)
            .addTag(packageName)
            .setInputData(workerData)
            .setInitialDelay(secondsDifference, TimeUnit.SECONDS)
            .build()

        workManager.enqueueUniquePeriodicWork(packageName, ExistingPeriodicWorkPolicy.REPLACE, reminderWorker)*/
    }

    fun cancelTrackedAppReminder(trackedApp: TrackedApp) {
        alarmManager.cancel(getPendingReminderIntent(trackedApp))
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

    fun scheduleTrackedAppsReminders(
        trackedApps: List<TrackedApp>
    ) = trackedApps.forEach {
        scheduleTrackedAppReminder(it)
    }

    companion object {

        fun canScheduleExactAlarms(
            context: Context
        ): Boolean {
            return when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                    (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager).canScheduleExactAlarms()
                }
                else -> true
            }
        }

    }

}