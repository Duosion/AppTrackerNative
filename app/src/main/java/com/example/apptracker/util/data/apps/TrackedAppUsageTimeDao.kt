package com.example.apptracker.util.data.apps

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackedAppUsageTimeDao {
    @Query("SELECT * FROM TrackedAppUsageTime")
    fun getAll(): Flow<List<TrackedAppUsageTime>>

    @Query("SELECT * FROM TrackedAppUsageTime WHERE firstTimestamp = (:timestamp) OR lastTimestamp = (:timestamp)")
    fun getAllWithTimestamp(timestamp: Long): Flow<List<TrackedAppUsageTime>>

    @Query("SELECT * FROM TrackedAppUsageTime WHERE (firstTimestamp BETWEEN (:timestampMin) and (:timestampMax))")
    fun getAllInTimestampRange(timestampMin: Long, timestampMax: Long): List<TrackedAppUsageTime>

    @Query("SELECT * FROM TrackedAppUsageTime WHERE packageName = (:packageName)")
    fun get(packageName: String): Flow<TrackedAppUsageTime?>

    @Query("SELECT SUM(usageTime) FROM TrackedAppUsageTime WHERE packageName = (:packageName)")
    fun getTotalUsageTime(packageName: String): Long

    @Query("SELECT id, packageName, firstTimestamp, lastTimestamp, usageTime FROM TrackedAppUsageTime WHERE packageName = (:packageName)")
    fun getUsageTimes(packageName: String): Flow<List<TrackedAppUsageTime>>

    @Query("SELECT id FROM TrackedAppUsageTime WHERE packageName = (:packageName) AND (firstTimestamp = (:timestamp) OR lastTimestamp = (:timestamp))")
    fun exists(packageName: String, timestamp: Long): Long?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg trackedApp: TrackedAppUsageTime)

    @Update
    fun update(trackedApp: TrackedAppUsageTime)
}