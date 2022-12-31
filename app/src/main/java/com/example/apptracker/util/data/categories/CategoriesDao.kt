package com.example.apptracker.util.data.categories

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoriesDao {
    @Query("SELECT * FROM Category")
    fun getAll(): Flow<List<Category>>

    @Query("SELECT * FROM Category WHERE id = (:categoryId)")
    fun get(categoryId: Int): Category?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg category: Category)

    @Query("UPDATE Category SET position = (:position) WHERE id = (:categoryId)")
    fun setPosition(categoryId: Int, position: Int)

    @Query("UPDATE Category SET name = (:name) WHERE id = (:categoryId)")
    fun setName(categoryId: Int, name: String)

    @Query("DELETE FROM Category WHERE id = (:categoryId)")
    fun delete(categoryId: Int)
}