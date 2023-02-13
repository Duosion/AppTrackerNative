package com.example.apptracker.ui.routes.settings.appearance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apptracker.util.data.AppDatabase
import com.example.apptracker.util.data.settings.Setting
import com.example.apptracker.util.data.settings.SettingsRepository
import com.example.apptracker.util.data.settings.values.DarkModeValues
import com.example.apptracker.util.data.settings.values.OledModeValues
import com.example.apptracker.util.data.settings.values.RandomThemeValues
import com.example.apptracker.util.data.settings.values.ThemeValues
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

class AppearanceViewModel (
    database: AppDatabase
) : ViewModel() {

    private val settingsRepository = SettingsRepository(database.settingsDao())

    private val _screenState = MutableStateFlow(AppearanceScreenState())
    val state: StateFlow<AppearanceScreenState> = _screenState.asStateFlow()

    init {
        refreshSettings()
    }

    private fun refreshSettings() = viewModelScope.launch {
        _screenState.value = AppearanceScreenState(isLoading = true)
        withContext(Dispatchers.IO) {
            val randomThemeValue = RandomThemeValues.fromId(settingsRepository.getSetting(Setting.RandomTheme))
            val themeValue = when(randomThemeValue) {
                RandomThemeValues.ON -> {
                    val currentValue = ThemeValues.fromId(settingsRepository.getSetting(Setting.Theme))
                    val values = ThemeValues.values().filter {
                        // prevents the user from getting the same theme two times in a row
                        it != currentValue
                    }
                    val size = values.size
                    val random = Random.nextInt(size - 1)
                    val randomValue = values[random]
                    setThemeValue(randomValue.id)
                    randomValue
                }
                RandomThemeValues.OFF -> ThemeValues.fromId(settingsRepository.getSetting(Setting.Theme))
            }
            _screenState.value = AppearanceScreenState(
                darkModeValue = DarkModeValues.fromId(settingsRepository.getSetting(Setting.DarkMode)),
                oledModeValue = OledModeValues.fromId(settingsRepository.getSetting(Setting.OledMode)),
                themeValue = themeValue,
                randomThemeValue = randomThemeValue
            )
        }
    }

    private fun setSetting(setting: Setting, id: Int) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            settingsRepository.setSetting(setting, id)
        }
    }

    fun setDarkModeValue(id: Int) {
        _screenState.update { currentState ->
            currentState.copy(darkModeValue = DarkModeValues.fromId(id))
        }
        setSetting(Setting.DarkMode, id)
    }

    fun setOledModeValue(id: Int) {
        _screenState.update { currentState ->
            currentState.copy(oledModeValue = OledModeValues.fromId(id))
        }
        setSetting(Setting.OledMode, id)
    }

    fun setThemeValue(id: Int) {
        _screenState.update { currentState ->
            currentState.copy(themeValue = ThemeValues.fromId(id))
        }
        setSetting(Setting.Theme, id)
    }

    fun setRandomThemeValue(id: Int) {
        _screenState.update { currentState ->
            currentState.copy(randomThemeValue = RandomThemeValues.fromId(id))
        }
        setSetting(Setting.RandomTheme, id)
    }


}