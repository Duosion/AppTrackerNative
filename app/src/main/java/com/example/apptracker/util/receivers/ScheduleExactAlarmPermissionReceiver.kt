package com.example.apptracker.util.receivers

import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build

class ScheduleExactAlarmPermissionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED && Build.VERSION.SDK_INT > Build.VERSION_CODES.S) {
            //val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            //val permissionStatus = alarmManager.canScheduleExactAlarms()
            OnBootReceiver().onReceive(context, intent)
        }
    }
}