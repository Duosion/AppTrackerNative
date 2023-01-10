package com.example.apptracker.util.data.apps

import androidx.room.*
import com.example.apptracker.util.apps.UsageTimeTimestamp
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackedAppOpenLogDao {
    @Query("SELECT * FROM TrackedAppOpenLog")
    fun getAll(): Flow<List<TrackedAppOpenLog>>

    @Query("SELECT * FROm TrackedAppOpenLog WHERE packageName = (:packageName) AND timestamp = (:timestamp)")
    fun get(packageName: String, timestamp: Long): TrackedAppOpenLog?

    @Query("SELECT * FROm TrackedAppOpenLog WHERE id = (:id)")
    fun get(id: Long): TrackedAppOpenLog?

    @Query("SELECT COUNT(*) FROM TrackedAppOpenLog WHERE packageName = (:packageName)")
    fun getOpenedStreak(packageName: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg openLog: TrackedAppOpenLog)

    @Update
    fun update(openLog: TrackedAppOpenLog)

    @Delete
    fun delete(openLog: TrackedAppOpenLog)
}