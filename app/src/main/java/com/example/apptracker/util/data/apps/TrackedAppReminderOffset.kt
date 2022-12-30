package com.example.apptracker.util.data.apps

import com.example.apptracker.R

enum class TrackedAppReminderOffset(val id: Int, val offsetHours: Int = 0, val offsetMinutes: Int = 0, val valueName: Int) {
    NO_OFFSET(0, valueName = R.string.apps_add_app_reminder_offset_no_offset_name),
    FIFTEEN_MINUTES(1,0,15,R.string.apps_add_app_reminder_offset_fifteen_minutes_name),
    THIRTY_MINUTES(2, 0, 30, R.string.apps_add_app_reminder_offset_thirty_minutes_name),
    ONE_HOUR(3,1,0,R.string.apps_add_app_reminder_offset_one_hour_name),
    CUSTOM(4,0,0,R.string.apps_add_app_reminder_offset_custom_name);

    companion object {
        private val map = values().associateBy { it.id }
        fun fromId (id: Int) = map[id] ?: NO_OFFSET
    }
}