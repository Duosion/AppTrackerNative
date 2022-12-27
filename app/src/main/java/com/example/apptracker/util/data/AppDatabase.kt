package com.example.apptracker.util.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.apptracker.util.data.settings.SettingsDao
import com.example.apptracker.util.data.settings.SettingsDataEntity

@Database(entities = [SettingsDataEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun settingsDao(): SettingsDao
}

fun getDatabase (
    context: Context,
): AppDatabase {
    return Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "app-database"
    ).build()
}