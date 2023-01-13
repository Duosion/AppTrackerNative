package com.example.apptracker.util.apps

import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import androidx.compose.foundation.lazy.layout.IntervalList
import com.example.apptracker.util.data.AppDatabase
import com.example.apptracker.util.data.apps.TrackedApp
import com.example.apptracker.util.data.apps.TrackedAppOpenLog
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
import kotlin.math.log

class TrackedAppsManager(
    database: AppDatabase,
    context: Context
) {

    private val trackedAppDao = database.trackedAppDao()
    private val usageTimeDao = database.usageTimeDao()
    private val openedLogDao = database.openLogDao()
    private val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

    private var allUsageStats: Map<String, UsageStats> = mapOf()
    private var allDailyUsageStats: Map<String, UsageStats> = mapOf()

    init {
        allUsageStats = getUsageStats(dayOffset = 7L)
        allDailyUsageStats = getUsageStats(offset = ZonedDateTime.now().offset)
    }
    private fun getUsageStats(
        offset: ZoneOffset = ZoneOffset.UTC,
        dayOffset: Long = 0L
    ): Map<String, UsageStats> {
        val timeNow = System.currentTimeMillis()
        val begin = LocalDateTime.ofEpochSecond(timeNow / 1000, 0, offset).let {
            LocalDateTime.of(it.year, it.monthValue, it.dayOfMonth, 0, 0)
                .minusDays(dayOffset)
                .toEpochSecond(offset) * 1000
        }
        val end = begin + (1000 * 60 * 60 * 24 * (dayOffset + 1))

        return usageStatsManager.queryAndAggregateUsageStats(
            begin,
            end
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
            val packageName = trackedApp.packageName
            trackedAppDao.update(trackedApp.copy(
                openedTimestamp = LocalDateTime.now(zoneId).toEpochSecond(zoneOffset),
                ignoreTimestamp = 0,
                openedToday = true
            ))
            val logTimestamp = LocalDateTime.now(zoneId).truncatedTo(ChronoUnit.DAYS).toEpochSecond(zoneOffset)
            openedLogDao.get(packageName, logTimestamp).let {
                if (it == null) {
                    openedLogDao.insert(TrackedAppOpenLog(
                        packageName = packageName,
                        timestamp = logTimestamp
                    ))
                }
            }
        } else {
            val useUTC = trackedApp.dayStartIsUTC
            val zoneOffset = if (useUTC) ZoneOffset.UTC else OffsetDateTime.now().offset
            val zoneId = if (useUTC) ZoneId.of("UTC") else ZoneId.systemDefault()

            trackedAppDao.update(trackedApp.copy(
                ignoreTimestamp = trackedApp.openedTimestamp,
                openedToday = false
            ))
            // remove log entry
            openedLogDao.get(trackedApp.packageName, LocalDateTime.now(zoneId).truncatedTo(ChronoUnit.DAYS).toEpochSecond(zoneOffset))?.let {
                openedLogDao.delete(it)
            }
        }
    }

    private fun updateTrackedAppUsageTime(trackedApp: TrackedApp, usageStats: UsageStats) {
        val firstTimestamp = usageStats.firstTimeStamp
        val lastTimestamp = usageStats.lastTimeStamp
        val packageName = trackedApp.packageName

        val existsId = usageTimeDao.exists(packageName, firstTimestamp)

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
        val packageName = trackedApp.packageName

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

        println("$packageName get usage stats - begin: ${LocalDateTime.ofEpochSecond(usageStats.firstTimeStamp / 1000, 0, ZonedDateTime.now().offset)} - end: ${LocalDateTime.ofEpochSecond(usageStats.lastTimeStamp / 1000, 0, ZonedDateTime.now().offset)}")


        // calculate streak
        val mostRecentTimestamp = openedLogDao.getMostRecent(packageName)
        val openedLogTimestamp = lastDateOpened.truncatedTo(ChronoUnit.DAYS).toEpochSecond(zoneOffset)
        val existingLogEntry = openedLogDao.get(packageName, openedLogTimestamp)

        mostRecentTimestamp?.let {
            if ((openedLogTimestamp - mostRecentTimestamp) > (60 * 60 * 24)) {
                openedLogDao.deleteAllWithPackageName(packageName)
            }
        }

        when {
            (!openedToday && existingLogEntry != null) -> {
                openedLogDao.delete(existingLogEntry)
            }
            (openedToday && existingLogEntry == null) -> {
                openedLogDao.insert(TrackedAppOpenLog(
                    packageName = packageName,
                    timestamp = openedLogTimestamp
                ))
            }
        }

        // update data
        trackedAppDao.update(trackedApp.copy(
            openedTimestamp = lastTimeUsed,
            openedToday = openedToday
        ))

        allDailyUsageStats[packageName]?.let { updateTrackedAppUsageTime(trackedApp, it) }
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