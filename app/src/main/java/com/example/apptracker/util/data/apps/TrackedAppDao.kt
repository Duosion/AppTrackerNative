package com.example.apptracker.util.data.apps

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TrackedAppDao {
    @Query("SELECT * FROM TrackedApp")
    fun getAll(): List<TrackedApp>

    @Query("SELECT * FROM TrackedApp WHERE packageName = (:packageName)")
    fun get(packageName: String): TrackedApp?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg trackedApp: TrackedApp)
}