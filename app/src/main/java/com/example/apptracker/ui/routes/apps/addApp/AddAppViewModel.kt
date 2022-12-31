package com.example.apptracker.ui.routes.apps.addApp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apptracker.util.data.AppDatabase
import com.example.apptracker.util.data.apps.TrackedApp
import com.example.apptracker.util.data.categories.CategoriesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalTime

class AddAppViewModel(
    database: AppDatabase,
    packageName: String
) : IAppPageViewModel, ViewModel() {

    private val categoriesRepository = CategoriesRepository(database.categoriesDao())
    private val trackedAppDao = database.trackedAppDao()

    private val _screenState = MutableStateFlow(AppScreenState(TrackedApp(packageName)))
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
                    categories = categoriesRepository.getCategories(showHidden = true)
                )
            }
            setLoading(false)
        }
    }

    override fun setDayStartTime(time: LocalTime) = viewModelScope.launch {
        _screenState.update {
            it.copy(
                trackedApp = _screenState.value.trackedApp.copy(
                    dayStartHour = time.hour,
                    dayStartMinute = time.minute
                )
            )
        }
    }

    override fun setDayStartIsUTC(value: Boolean) = viewModelScope.launch {
        _screenState.update {
            it.copy(
                trackedApp = _screenState.value.trackedApp.copy(
                    dayStartIsUTC = value
                )
            )
        }
    }

    override fun setCategoryId(value: Int) = viewModelScope.launch {
        _screenState.update {
            it.copy(
                trackedApp = _screenState.value.trackedApp.copy(
                    categoryId = value
                )
            )
        }
    }

    override fun setReminderNotification(value: Boolean) = viewModelScope.launch {
        _screenState.update {
            it.copy(
                trackedApp = _screenState.value.trackedApp.copy(
                    reminderNotification = value
                )
            )
        }
    }

    override fun setReminderOffset(value: Int) = viewModelScope.launch {
        _screenState.update {
            it.copy(
                trackedApp = _screenState.value.trackedApp.copy(
                    reminderOffset = value
                )
            )
        }
    }

    override fun setCustomReminderOffsetTime(time: LocalTime) = viewModelScope.launch {
        _screenState.update {
            it.copy(
                trackedApp = _screenState.value.trackedApp.copy(
                    reminderOffsetHour = time.hour,
                    reminderOffsetMinute = time.minute
                )
            )
        }
    }

    override fun addTrackedApp() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            trackedAppDao.insert(_screenState.value.trackedApp)
        }
    }

    override fun deleteTrackedApp() = viewModelScope.launch { }

}