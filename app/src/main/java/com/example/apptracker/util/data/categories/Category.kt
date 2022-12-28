package com.example.apptracker.util.data.categories

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Category (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var hidden: Boolean = false,
    var name: String,
    var position: Int,
)