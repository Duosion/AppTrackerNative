package com.example.apptracker.util.data.apps

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TrackedApp (
    // the package name of this app.
    val packageName: String,
    // the UID of the app this belongs to
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    // the hour when the day starts for this app.
    var dayStartHour: Int = 0,
    // the minute when the day starts for this app.
    var dayStartMinute: Int = 0,
    // is the dayStartHour and dayStartMinute values in UTC?
    var dayStartIsUTC: Boolean = true,
    // is the reminder notification enabled?
    var reminderNotification: Boolean = false,
    // how far away from the dayStart the reminder will be sent. Value is the ID of a TrackedAppReminderOffset enum.
    var reminderOffset: Int = 2,
    // if reminderOffset is set to CUSTOM, these values will describe specifically when the reminder should occur.
    var reminderOffsetHour: Int = 0,
    var reminderOffsetMinute: Int = 0,
    // the category that this app belongs to.
    var categoryId: Int = 1,
    // has this app been opened today?
    var openedToday: Boolean = false,
    // the unix timestamp when this app was opened
    var openedTimestamp: Long = 0,
    // the timestamp to ignore
    var ignoreTimestamp: Long = 1,
    // how many days in a row has this app been opened without missing a day?
    var openStreak: Int = 0
)