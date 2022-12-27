package com.example.apptracker.util.data.settings

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SettingsDataEntity(
    @PrimaryKey val key: String,
    @ColumnInfo(name = "value") val value: Int
)
