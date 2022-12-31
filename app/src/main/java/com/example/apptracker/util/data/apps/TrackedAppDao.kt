package com.example.apptracker.util.data.apps

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackedAppDao {
    @Query("SELECT * FROM TrackedApp")
    fun getAll(): Flow<List<TrackedApp>>

    @Query("SELECT * FROM TrackedApp WHERE packageName = (:packageName)")
    fun get(packageName: String): Flow<TrackedApp?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg trackedApp: TrackedApp)

    @Update
    fun update(trackedApp: TrackedApp)

    @Delete
    fun delete(trackedApp: TrackedApp)
}