package com.example.apptracker.util.data.settings

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SettingsDao {
    @Query("SELECT * FROM settingsdataentity")
    fun getAll(): List<SettingsDataEntity>

    @Query("SELECT * FROM settingsdataentity WHERE key = (:settingKey)")
    fun get(settingKey: String): SettingsDataEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg user: SettingsDataEntity)

}