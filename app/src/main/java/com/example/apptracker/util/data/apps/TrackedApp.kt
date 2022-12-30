package com.example.apptracker.util.data.apps

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TrackedApp (
    // the package name of this app.
    @PrimaryKey val packageName: String,
    // the hour when the day starts for this app.
    var dayStartHour: Int = 0,
    // the minute when the day starts for this app.
    var dayStartMinute: Int = 0,
    // is the dayStartHour and dayStartMinute values in UTC?
    var dayStartIsUTC: Boolean = true,
    // the category that this app belongs to.
    var categoryId: Int = 1,
    // has this app been opened today?
    var openedToday: Boolean = false,
    // the unix timestamp when this app was opened
    var openedTimestamp: Long = 0,
    // how many days in a row has this app been opened without missing a day?
    var openStreak: Int = 0
)