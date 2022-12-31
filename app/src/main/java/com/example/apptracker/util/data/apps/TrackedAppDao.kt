package com.example.apptracker.util.data.apps

import androidx.room.*

@Dao
interface TrackedAppDao {
    @Query("SELECT * FROM TrackedApp")
    fun getAll(): List<TrackedApp>

    @Query("SELECT * FROM TrackedApp WHERE packageName = (:packageName)")
    fun get(packageName: String): TrackedApp?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg trackedApp: TrackedApp)

    @Update
    fun update(trackedApp: TrackedApp)

    @Delete
    fun delete(trackedApp: TrackedApp)
}