package com.example.apptracker.util.data.tabs

import androidx.room.*
import com.example.apptracker.util.data.apps.TrackedApp
import kotlinx.coroutines.flow.Flow

@Dao
interface TabStateDao {
    @Query("SELECT * FROM TabState WHERE id = (:stateId)")
    fun get(stateId: String): Flow<TabState?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg tabState: TabState)
}