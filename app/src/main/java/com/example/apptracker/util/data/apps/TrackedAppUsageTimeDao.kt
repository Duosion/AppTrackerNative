package com.example.apptracker.util.data.apps

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackedAppUsageTimeDao {
    @Query("SELECT * FROM TrackedAppUsageTime")
    fun getAll(): Flow<List<TrackedAppUsageTime>>

    @Query("SELECT * FROM TrackedAppUsageTime WHERE timestamp = (:timestamp)")
    fun getAllWithTimestamp(timestamp: String): Flow<List<TrackedAppUsageTime>>

    @Query("SELECT * FROM TrackedAppUsageTime WHERE packageName = (:packageName)")
    fun get(packageName: String): Flow<TrackedAppUsageTime?>

    @Query("SELECT SUM(usageTime) FROM TrackedAppUsageTime WHERE packageName = (:packageName)")
    fun getTotalUsageTime(packageName: String): Long

    @Query("SELECT packageName, timestamp, usageTime FROM TrackedAppUsageTime WHERE packageName = (:packageName)")
    fun getUsageTimes(packageName: String): Flow<List<TrackedAppUsageTime>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg trackedApp: TrackedAppUsageTime)

    @Update
    fun update(trackedApp: TrackedAppUsageTime)
}