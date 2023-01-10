package com.example.apptracker.util.apps

enum class UsageTimeGroupBy(val timeUnit: Long) {
    DAY(1000 * 60 * 60 * 24),
    WEEK(1000 * 60 * 60 * 24 * 7);
}