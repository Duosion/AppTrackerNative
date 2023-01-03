package com.example.apptracker.util.apps

import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import com.example.apptracker.util.data.AppDatabase
import com.example.apptracker.util.data.apps.TrackedApp
import kotlinx.coroutines.flow.firstOrNull
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAmount
import java.util.Calendar
import java.util.Date

class TrackedAppsManager(
    database: AppDatabase,
    context: Context
) {

    private val trackedAppDao = database.trackedAppDao()
    private val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

    private var allUsageStats: Map<String, List<UsageStats>> = mapOf()

    init {
        allUsageStats = getUsageStats()
    }

    private fun getUsageStats(): Map<String, List<UsageStats>> {
        val timeNow = LocalDateTime.now()
        val begin = timeNow.minusDays(1L)

        val stats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_WEEKLY, begin.toEpochSecond(ZoneOffset.UTC) * 1000, timeNow.toEpochSecond(ZoneOffset.UTC) * 1000)

        return stats.groupBy { it.packageName }
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
    }

    fun updateTrackedAppOpenedStatus(trackedApp: TrackedApp) {
        allUsageStats[trackedApp.packageName]?.let {
            updateTrackedAppOpenedStatus(trackedApp, it.first())
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


}