package com.example.apptracker.util.apps

import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import com.example.apptracker.util.data.AppDatabase
import com.example.apptracker.util.data.apps.TrackedApp
import com.example.apptracker.util.data.apps.TrackedAppUsageTime
import kotlinx.coroutines.flow.firstOrNull
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class TrackedAppsManager(
    database: AppDatabase,
    context: Context
) {

    private val trackedAppDao = database.trackedAppDao()
    private val usageTimeDao = database.usageTimeDao()
    private val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

    private var allUsageStats: Map<String, UsageStats> = mapOf()
    private var allDailyUsageStats: Map<String, UsageStats> = mapOf()

    init {
        allUsageStats = getUsageStats(7L)
        allDailyUsageStats = getUsageStats()
    }

    private fun getUsageStats(
        dayOffset: Long = 0L
    ): Map<String, UsageStats> {
        val offset = ZoneOffset.UTC
        val timeNow = System.currentTimeMillis()
        val begin = LocalDateTime.ofEpochSecond(timeNow / 1000, 0, offset).let {
            LocalDateTime.of(it.year, it.monthValue, it.dayOfMonth, 0, 0)
                .minusDays(dayOffset)
                .toEpochSecond(offset) * 1000
        }

        return usageStatsManager.queryAndAggregateUsageStats(
            begin,
            timeNow
        )
    }

    fun refreshUsageStats() {
        allUsageStats = getUsageStats()
    }

    fun setTrackedAppOpenedStatus(trackedApp: TrackedApp, isOpened: Boolean = true) {
        if (isOpened) {
            val useUTC = trackedApp.dayStartIsUTC
            val zoneOffset = if (useUTC) ZoneOffset.UTC else OffsetDateTime.now().offset
            val zoneId = if (useUTC) ZoneId.of("UTC") else ZoneId.systemDefault()
            trackedAppDao.update(trackedApp.copy(
                openedTimestamp = LocalDateTime.now(zoneId).toEpochSecond(zoneOffset),
                ignoreTimestamp = 0,
                openedToday = true
            ))
        } else {
            trackedAppDao.update(trackedApp.copy(
                ignoreTimestamp = trackedApp.openedTimestamp,
                openedToday = false
            ))
        }
    }

    private fun updateTrackedAppUsageTime(trackedApp: TrackedApp, usageStats: UsageStats) {
        val firstTimestamp = usageStats.firstTimeStamp
        val lastTimestamp = usageStats.lastTimeStamp
        val packageName = trackedApp.packageName

        val existsId = usageTimeDao.exists(packageName, lastTimestamp)

        if (existsId == null) {
            // insert new
            usageTimeDao.insert(TrackedAppUsageTime(
                packageName = packageName,
                firstTimestamp = firstTimestamp,
                lastTimestamp = lastTimestamp,
                usageTime = usageStats.totalTimeInForeground
            ))
        } else {
            // update existing
            usageTimeDao.update(TrackedAppUsageTime(
                id = existsId,
                packageName = packageName,
                firstTimestamp = firstTimestamp,
                lastTimestamp = lastTimestamp,
                usageTime = usageStats.totalTimeInForeground
            ))
        }

    }

    private fun updateTrackedAppOpenedStatus(trackedApp: TrackedApp, usageStats: UsageStats) {
        val useUTC = trackedApp.dayStartIsUTC

        val lastTimeUsed = trackedApp.openedTimestamp.coerceAtLeast(usageStats.lastTimeUsed / 1000)
        val zoneOffset = if (useUTC) ZoneOffset.UTC else OffsetDateTime.now().offset

        val lastDateOpened = LocalDateTime.ofEpochSecond(if (trackedApp.ignoreTimestamp == lastTimeUsed) 0 else lastTimeUsed, 0, zoneOffset)

        val dayStartDate = LocalDateTime.of(LocalDate.now(zoneOffset), LocalTime.of(trackedApp.dayStartHour, trackedApp.dayStartMinute)).let {
            if (useUTC && it > LocalDateTime.now(ZoneId.of("UTC"))) {
                it.minusDays(1L)
            } else {
                it
            }
        }

        val openedToday = lastDateOpened >= dayStartDate

        // calculate streak
        val daysDifference = ChronoUnit.DAYS.between(lastDateOpened, dayStartDate)
        val streakAlreadySet = trackedApp.openedTimestamp > dayStartDate.toEpochSecond(zoneOffset)

        // update data
        trackedAppDao.update(trackedApp.copy(
            openedTimestamp = lastTimeUsed,
            openedToday = openedToday,
            openStreak = when {
                streakAlreadySet -> trackedApp.openStreak
                2 > daysDifference -> trackedApp.openStreak + 1
                else -> 0
            }
        ))

        allDailyUsageStats[trackedApp.packageName]?.let { updateTrackedAppUsageTime(trackedApp, it) }
    }

    fun updateTrackedAppOpenedStatus(trackedApp: TrackedApp) {
        allUsageStats[trackedApp.packageName]?.let {
            updateTrackedAppOpenedStatus(trackedApp, it)
        }
    }

    suspend fun updateTrackedAppOpenedStatus(packageName: String) {
        trackedAppDao.get(packageName).firstOrNull()?.let {
            updateTrackedAppOpenedStatus(it)
        }
    }

    suspend fun updateTrackedAppsOpenedStatus() {
        trackedAppDao.getAll().firstOrNull()?.let {
            it.forEach { trackedApp ->
                updateTrackedAppOpenedStatus(trackedApp)
            }
        }
    }

    companion object {
        fun getTimestamp(
            epochMillisecond: Long = System.currentTimeMillis()
        ): String {
            return LocalDateTime.ofEpochSecond(epochMillisecond / 1000, 0, ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        }
    }


}