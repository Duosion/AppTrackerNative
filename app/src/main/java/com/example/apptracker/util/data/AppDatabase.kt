package com.example.apptracker.util.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.apptracker.util.data.apps.TrackedApp
import com.example.apptracker.util.data.apps.TrackedAppDao
import com.example.apptracker.util.data.apps.TrackedAppUsageTime
import com.example.apptracker.util.data.apps.TrackedAppUsageTimeDao
import com.example.apptracker.util.data.categories.Category
import com.example.apptracker.util.data.categories.CategoriesDao
import com.example.apptracker.util.data.settings.SettingsDao
import com.example.apptracker.util.data.settings.SettingsDataEntity
import com.example.apptracker.util.data.tabs.TabState
import com.example.apptracker.util.data.tabs.TabStateDao

@Database(entities = [SettingsDataEntity::class,Category::class,TrackedApp::class,TabState::class,TrackedAppUsageTime::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun settingsDao(): SettingsDao
    abstract fun categoriesDao(): CategoriesDao
    abstract fun trackedAppDao(): TrackedAppDao
    abstract fun tabStateDao(): TabStateDao
    abstract fun usageTimeDao(): TrackedAppUsageTimeDao
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
