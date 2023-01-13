package com.example.apptracker.util.data.apps

import androidx.room.*
import com.example.apptracker.util.apps.UsageTimeTimestamp
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackedAppOpenLogDao {
    @Query("SELECT * FROM TrackedAppOpenLog")
    fun getAll(): Flow<List<TrackedAppOpenLog>>

    @Query("SELECT * FROM TrackedAppOpenLog WHERE packageName = (:packageName) AND timestamp = (:timestamp)")
    fun get(packageName: String, timestamp: Long): TrackedAppOpenLog?

    @Query("SELECT * FROM TrackedAppOpenLog WHERE id = (:id)")
    fun get(id: Long): TrackedAppOpenLog?

    @Query("SELECT max(timestamp) as timestamp FROM TrackedAppOpenLog WHERE packageName = (:packageName)")
    fun getMostRecent(packageName: String): Long?

    @Query("SELECT COUNT(*) FROM TrackedAppOpenLog WHERE packageName = (:packageName)")
    fun getOpenedStreak(packageName: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg openLog: TrackedAppOpenLog)

    @Update
    fun update(openLog: TrackedAppOpenLog)

    @Delete
    fun delete(openLog: TrackedAppOpenLog)

    @Query("DELETE FROM TrackedAppOpenLog WHERE packageName = (:packageName)")
    fun deleteAllWithPackageName(packageName: String)
}