package com.example.apptracker.ui.routes.apps.addApp

import androidx.compose.runtime.Stable
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

class AddAppViewModel(
    database: AppDatabase,
    packageName: String
) : ViewModel() {

    private val categoriesRepository = CategoriesRepository(database.categoriesDao())
    private val trackedAppDao = database.trackedAppDao()

    private val _screenState = MutableStateFlow(AddAppScreenState(TrackedApp(packageName)))
    val state: StateFlow<AddAppScreenState> = _screenState.asStateFlow()

    init {
        refreshCategories()
    }

    private fun refreshCategories() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            _screenState.update {
                it.copy(
                    categories = categoriesRepository.getCategories(showHidden = true)
                )
            }
        }
    }

    fun setDayStartTime(time: LocalTime) = viewModelScope.launch {
        _screenState.update {
            it.copy(
                trackedApp = _screenState.value.trackedApp.copy(
                    dayStartHour = time.hour,
                    dayStartMinute = time.minute
                )
            )
        }
    }

    fun setDayStartIsUTC(value: Boolean) = viewModelScope.launch {
        _screenState.update {
            it.copy(
                trackedApp = _screenState.value.trackedApp.copy(
                    dayStartIsUTC = value
                )
            )
        }
    }

    fun setCategoryId(value: Int) = viewModelScope.launch {
        _screenState.update {
            it.copy(
                trackedApp = _screenState.value.trackedApp.copy(
                    categoryId = value
                )
            )
        }
    }

    fun addTrackedApp() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            trackedAppDao.insert(_screenState.value.trackedApp)
        }
    }

}