package com.example.apptracker.util.data.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepository(
    private val dao: SettingsDao
) {

    fun getSetting(setting: Setting): Int {
        return dao.get(setting.key)?.value ?: setting.defaultValue
    }

    fun setSetting(setting: Setting, value: Int) {
        dao.insert(SettingsDataEntity(
            key = setting.key,
            value = value
        ))
    }

}