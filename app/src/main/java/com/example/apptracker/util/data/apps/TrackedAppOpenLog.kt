package com.example.apptracker.util.data.apps

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date

@Entity
data class TrackedAppOpenLog (
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    // the package name of this app.
    val packageName: String,
    // the epoch timestamp for this usage time
    val timestamp: Long,
)