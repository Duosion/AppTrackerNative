package com.example.apptracker.util.data.tabs

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TabState (
    @PrimaryKey
    val id: String,
    val page: Int = 0,
)