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

class AddAppViewModel(
    database: AppDatabase,
    packageName: String,
    private val notificationChannel: AppNotificationChannel
) : IAppPageViewModel, ViewModel() {

    private val categoriesRepository = CategoriesRepository(database.categoriesDao())
    private val trackedAppDao = database.trackedAppDao()

    private val _trackedAppState = MutableStateFlow(TrackedApp(packageName))
    private val _screenState = MutableStateFlow(AppScreenState(_trackedAppState.asStateFlow()))
    override val state: StateFlow<AppScreenState> = _screenState.asStateFlow()

    init {
        refreshCategories()
    }

    private fun setLoading(isLoading: Boolean = true) {
        _screenState.update {
            it.copy(
                isLoading = isLoading
            )
        }
    }

    private fun refreshCategories() = viewModelScope.launch {
        setLoading()
        withContext(Dispatchers.IO) {
            _screenState.update {
                it.copy(
                    categories = categoriesRepository.getCategories(showHidden = true).first()
                )
            }
            setLoading(false)
        }
    }

    override fun setDayStartTime(time: LocalTime) = viewModelScope.launch {
        _trackedAppState.update {
            it.copy(
                dayStartHour = time.hour,
                dayStartMinute = time.minute
            )
        }
    }

    override fun setDayStartIsUTC(value: Boolean) = viewModelScope.launch {
        _trackedAppState.update {
            it.copy(
                dayStartIsUTC = value
            )
        }
    }

    override fun setCategoryId(value: Int) = viewModelScope.launch {
        _trackedAppState.update {
            it.copy(
                categoryId = value
            )
        }
    }

    override fun setReminderNotification(value: Boolean) = viewModelScope.launch {
        _trackedAppState.update {
            it.copy(
                reminderNotification = value
            )
        }
    }

    override fun setReminderOffset(value: Int) = viewModelScope.launch {
        _trackedAppState.update {
            it.copy(
                reminderOffset = value
            )
        }
    }

    override fun setCustomReminderOffsetTime(time: LocalTime) = viewModelScope.launch {
        _trackedAppState.update {
            it.copy(
                reminderOffsetHour = time.hour,
                reminderOffsetMinute = time.minute
            )
        }
    }

    override fun addTrackedApp() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val toAdd = _screenState.value.trackedApp.first()
            if (toAdd != null) {
                trackedAppDao.insert(toAdd)
                notificationChannel.scheduleTrackedAppReminder(toAdd)
            }
        }
    }

    override fun deleteTrackedApp() = viewModelScope.launch { }

}