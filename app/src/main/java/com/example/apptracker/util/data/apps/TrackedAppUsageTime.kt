package com.example.apptracker.util.data.apps

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date

@Entity
data class TrackedAppUsageTime (
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    // the package name of this app.
    val packageName: String,
    // the epoch timestamp for this usage time
    val firstTimestamp: Long,
    val lastTimestamp: Long,
    // the usage time in milliseconds
    var usageTime: Long = 0,
)