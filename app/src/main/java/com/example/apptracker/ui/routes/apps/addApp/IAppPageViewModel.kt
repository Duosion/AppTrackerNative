package com.example.apptracker.ui.routes.apps.addApp

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalTime

interface IAppPageViewModel {

    val state: StateFlow<AppScreenState>

    fun setDayStartTime(time: LocalTime): Job?

    fun setDayStartIsUTC(value: Boolean): Job?

    fun setCategoryId(value: Int): Job?

    fun setReminderNotification(value: Boolean): Job?

    fun setReminderOffset(value: Int): Job?

    fun setCustomReminderOffsetTime(time: LocalTime): Job?

    fun addTrackedApp(): Job?

    fun deleteTrackedApp(): Job?

}