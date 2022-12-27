package com.example.apptracker.ui.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apptracker.util.data.AppDatabase
import com.example.apptracker.util.data.settings.Setting
import com.example.apptracker.util.data.settings.SettingsRepository
import com.example.apptracker.util.data.settings.values.DarkModeValues
import com.example.apptracker.util.data.settings.values.DynamicColorModeValues
import com.example.apptracker.util.data.settings.values.OledModeValues
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ThemeViewModel (
    database: AppDatabase
) : ViewModel() {

    private val settingsRepository = SettingsRepository(database.settingsDao())

    private val _screenState = MutableStateFlow(ThemeState())
    val state: StateFlow<ThemeState> = _screenState.asStateFlow()

    init {
        refreshValues()
    }

    private fun refreshValues() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            _screenState.value = ThemeState(
                darkModeValue = DarkModeValues.fromId(settingsRepository.getSetting(Setting.DarkMode)),
                oledModeValue = OledModeValues.fromId(settingsRepository.getSetting(Setting.OledMode)),
                dynamicColorModeValue = DynamicColorModeValues.fromId(settingsRepository.getSetting(Setting.DynamicColorMode))
            )
        }
    }

}