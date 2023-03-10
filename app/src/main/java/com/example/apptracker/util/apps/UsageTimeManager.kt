package com.example.apptracker.util.apps

import com.example.apptracker.util.data.AppDatabase
import com.example.apptracker.util.data.apps.TrackedAppUsageTime
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

class UsageTimeManager(
    database: AppDatabase
) {
    private val usageTimeDao = database.usageTimeDao()

    fun queryUsageTime(
        beginTime: Long,
        endTime: Long,
        groupBy: UsageTimeGroupBy = UsageTimeGroupBy.DAY,
        zoneOffset: ZoneOffset = ZoneOffset.UTC
    ): List<GroupedUsageTime> {

        val inRange = usageTimeDao.getAllInTimestampRange(beginTime, endTime)

        // get groups
        val groups: MutableMap<UsageTimeTimestamp, List<TrackedAppUsageTime>> = mutableMapOf()
        val steps = ((endTime - beginTime) / groupBy.timeUnit).toInt()
        for (index in (steps - 1) downTo 0) {
            val timestamp = endTime - (groupBy.timeUnit * index)
            val timestampDate = LocalDateTime.ofEpochSecond(timestamp / 1000, 0, zoneOffset)
            groups[UsageTimeTimestamp(
                date = timestampDate.truncatedTo(ChronoUnit.DAYS)
            )] = listOf()
        }

        // group
        val grouped = inRange.groupBy {
            when(groupBy) {
                UsageTimeGroupBy.DAY -> {
                    val timestampDate = LocalDateTime.ofEpochSecond(it.lastTimestamp / 1000, 0, zoneOffset)
                    UsageTimeTimestamp(
                        date = timestampDate.truncatedTo(ChronoUnit.DAYS)
                    )
                }
                UsageTimeGroupBy.WEEK -> {
                    val timestampDate = LocalDateTime.ofEpochSecond(it.lastTimestamp / 1000, 0, zoneOffset)
                    timestampDate.truncatedTo(ChronoUnit.WEEKS)
                    UsageTimeTimestamp(
                        date = timestampDate.truncatedTo(ChronoUnit.DAYS).minusDays((timestampDate.dayOfWeek ?: DayOfWeek.SUNDAY).value.toLong())
                    )
                }
            }
        }

        // combine alike package usage times (no more than one entry of the same package name in each group)
        val merged: MutableList<GroupedUsageTime> = mutableListOf()
        groups.plus(grouped).forEach { (usageTimeTimestamp, usageTimes) ->

            val lookup: MutableMap<String, TrackedAppUsageTime> = mutableMapOf()

            var combinedUsageTime = 0L

            usageTimes.forEach {
                val packageName = it.packageName
                val existing = lookup[packageName]
                val usageTime = it.usageTime
                if (existing == null) {
                    lookup[packageName] = it
                } else {
                    existing.usageTime += usageTime
                }

                combinedUsageTime += usageTime
            }

            merged.add(GroupedUsageTime(
                timestamp = usageTimeTimestamp,
                combinedUsageTime = combinedUsageTime,
                values = lookup.values.toList().sortedByDescending { it.usageTime }
            ))
        }

        return merged.toList()
    }

    // returns the combined view time of packages within the provided range
    fun queryCombinedUsageTime(
        beginTime: Long,
        endTime: Long
    ): GroupedUsageTime {

        val inRange = usageTimeDao.getAllInTimestampRange(beginTime, endTime)

        val grouped = inRange.groupBy {
            it.packageName
        }

        val merged: MutableMap<String, TrackedAppUsageTime> = mutableMapOf()
        var combinedUsageTime = 0L
        grouped.forEach { (packageName, usageTimes) ->
            usageTimes.forEach {
                val existing = merged[packageName]
                val usageTime = it.usageTime
                if (existing == null) {
                    merged[packageName] = it
                } else {
                    existing.usageTime += usageTime
                }
                combinedUsageTime += usageTime
            }
        }

        return GroupedUsageTime(
            timestamp = UsageTimeTimestamp(
                date = LocalDateTime.now(),
            ),
            combinedUsageTime = combinedUsageTime,
            values = merged.values.toList().sortedByDescending { it.usageTime }
        )
    }

}