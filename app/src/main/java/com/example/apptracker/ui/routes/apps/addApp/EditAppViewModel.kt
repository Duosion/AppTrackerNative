package com.example.apptracker.ui.routes.apps.addApp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apptracker.util.data.AppDatabase
import com.example.apptracker.util.data.apps.TrackedApp
import com.example.apptracker.util.data.categories.CategoriesRepository
import com.example.apptracker.util.notifications.AppNotificationChannel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalTime

class EditAppViewModel(
    database: AppDatabase,
    private val packageName: String,
    private val notificationChannel: AppNotificationChannel
) : IAppPageViewModel, ViewModel() {

    private val categoriesRepository = CategoriesRepository(database.categoriesDao())
    private val trackedAppDao = database.trackedAppDao()

    private val _screenState = MutableStateFlow(AppScreenState(MutableStateFlow(TrackedApp(packageName))))
    override val state: StateFlow<AppScreenState> = _screenState.asStateFlow()

    init {
        refresh()
    }

    private fun setLoading(isLoading: Boolean = true) {
        _screenState.update {
            it.copy(
                isLoading = isLoading
            )
        }
    }

    private fun refresh() = viewModelScope.launch {
        setLoading()
        withContext(Dispatchers.IO) {
            _screenState.update {
                it.copy(
                    trackedApp = trackedAppDao.get(packageName),
                    categories = categoriesRepository.getCategories(showHidden = true).first()
                )
            }
            setLoading(false)
        }
    }

    override fun setDayStartTime(time: LocalTime) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            _screenState.value.trackedApp.firstOrNull()?.let {
                trackedAppDao.update(
                    it.copy(
                        dayStartHour = time.hour,
                        dayStartMinute = time.minute
                    )
                )
            }
        }
    }

    override fun setDayStartIsUTC(value: Boolean) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            _screenState.value.trackedApp.firstOrNull()?.let {
                val updated = it.copy(
                    dayStartIsUTC = value
                )
                trackedAppDao.update(updated)
                notificationChannel.scheduleTrackedAppReminder(updated)
            }
        }
    }

    override fun setCategoryId(value: Int) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            _screenState.value.trackedApp.first()?.let {
                trackedAppDao.update(
                    it.copy(
                        categoryId = value
                    ))
            }
        }
    }

    override fun setReminderNotification(value: Boolean) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            _screenState.value.trackedApp.first()?.let {
                val updated = it.copy(
                    reminderNotification = value
                )
                trackedAppDao.update(updated)
                if (value) {
                    notificationChannel.scheduleTrackedAppReminder(updated)
                } else {
                    notificationChannel.cancelTrackedAppReminder(updated)
                }
            }
        }
    }

    override fun setReminderOffset(value: Int) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            _screenState.value.trackedApp.first()?.let {
                val updated = it.copy(
                    reminderOffset = value
                )
                trackedAppDao.update(updated)
                notificationChannel.scheduleTrackedAppReminder(updated)
            }
        }
    }

    override fun setCustomReminderOffsetTime(time: LocalTime) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            _screenState.value.trackedApp.first()?.let {
                val updated = it.copy(
                    reminderOffsetHour = time.hour,
                    reminderOffsetMinute = time.minute
                )
                trackedAppDao.update(updated)
                notificationChannel.scheduleTrackedAppReminder(updated)
            }
        }
    }

    override fun addTrackedApp() = viewModelScope.launch { }

    override fun deleteTrackedApp() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val toDelete = _screenState.value.trackedApp.first()
            if (toDelete != null) {
                notificationChannel.cancelTrackedAppReminder(toDelete)
                trackedAppDao.delete(toDelete)
            }
        }
    }

}