package com.example.apptracker.util.data.apps

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date

@Entity
data class TrackedAppUsageTime (
    // the package name of this app.
    @PrimaryKey val packageName: String,
    // the date of this data entry
    val timestamp: String,
    // the usage time in milliseconds
    val usageTime: Long = 0,

)