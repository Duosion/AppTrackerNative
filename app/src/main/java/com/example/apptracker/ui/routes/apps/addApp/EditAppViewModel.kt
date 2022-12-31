package com.example.apptracker.ui.routes.apps.addApp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apptracker.util.data.AppDatabase
import com.example.apptracker.util.data.apps.TrackedApp
import com.example.apptracker.util.data.categories.CategoriesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalTime

class EditAppViewModel(
    database: AppDatabase,
    private val packageName: String
) : IAppPageViewModel, ViewModel() {

    private val categoriesRepository = CategoriesRepository(database.categoriesDao())
    private val trackedAppDao = database.trackedAppDao()

    private val _screenState = MutableStateFlow(AppScreenState(TrackedApp(packageName)))
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
            val trackedApp = trackedAppDao.get(packageName)
            trackedApp?.let {
                _screenState.update {
                    it.copy(
                        trackedApp = trackedApp,
                        categories = categoriesRepository.getCategories(showHidden = true)
                    )
                }
                setLoading(false)
            }
        }
    }

    override fun setDayStartTime(time: LocalTime) = viewModelScope.launch {
        val updatedTrackedApp = _screenState.value.trackedApp.copy(
            dayStartHour = time.hour,
            dayStartMinute = time.minute
        )
        _screenState.update {
            it.copy(
                trackedApp = updatedTrackedApp
            )
        }
        withContext(Dispatchers.IO) {
            trackedAppDao.update(updatedTrackedApp)
        }
    }

    override fun setDayStartIsUTC(value: Boolean) = viewModelScope.launch {
        val updated = _screenState.value.trackedApp.copy(
            dayStartIsUTC = value
        )
        _screenState.update {
            it.copy(
                trackedApp = updated
            )
        }
        withContext(Dispatchers.IO) {
            trackedAppDao.update(updated)
        }
    }

    override fun setCategoryId(value: Int) = viewModelScope.launch {
        val updated = _screenState.value.trackedApp.copy(
            categoryId = value
        )
        _screenState.update {
            it.copy(
                trackedApp = updated
            )
        }
        withContext(Dispatchers.IO) {
            trackedAppDao.update(updated)
        }
    }

    override fun setReminderNotification(value: Boolean) = viewModelScope.launch {
        val updated = _screenState.value.trackedApp.copy(
            reminderNotification = value
        )
        _screenState.update {
            it.copy(
                trackedApp = updated
            )
        }
        withContext(Dispatchers.IO) {
            trackedAppDao.update(updated)
        }
    }

    override fun setReminderOffset(value: Int) = viewModelScope.launch {
        val updated = _screenState.value.trackedApp.copy(
            reminderOffset = value
        )
        _screenState.update {
            it.copy(
                trackedApp = updated
            )
        }
        withContext(Dispatchers.IO) {
            trackedAppDao.update(updated)
        }
    }

    override fun setCustomReminderOffsetTime(time: LocalTime) = viewModelScope.launch {
        val updated = _screenState.value.trackedApp.copy(
            reminderOffsetHour = time.hour,
            reminderOffsetMinute = time.minute
        )
        _screenState.update {
            it.copy(
                trackedApp = updated
            )
        }
        withContext(Dispatchers.IO) {
            trackedAppDao.update(updated)
        }
    }

    override fun addTrackedApp() = viewModelScope.launch { }

    override fun deleteTrackedApp() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            trackedAppDao.delete(_screenState.value.trackedApp)
        }
    }

}