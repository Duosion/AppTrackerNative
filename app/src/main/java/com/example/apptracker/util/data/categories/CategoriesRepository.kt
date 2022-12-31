package com.example.apptracker.util.data.categories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

class CategoriesRepository(
    private val dao: CategoriesDao
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getCategories(
        showHidden: Boolean = false
    ): Flow<List<Category>> {
        val items = dao.getAll()
       return items.mapLatest {
            if (it.isEmpty()) {
                val newCategory = Category(
                    id = 0,
                    name = "Default",
                    position = 0,
                    hidden = true
                )
                withContext(Dispatchers.IO) {
                    addCategory(newCategory)
                }
                it + listOf(newCategory)
            } else {
                it.filterNot { item -> item.hidden && !showHidden }.sortedBy { item -> item.position }
            }
        }
    }

    fun addCategory(category: Category) {
        dao.insert(category)
    }

    fun setPosition(category: Category, newPosition: Int) {
        dao.setPosition(category.id, newPosition)
    }

    fun setName(category: Category, name: String) {
        dao.setName(category.id, name)
    }

    suspend fun deleteCategory(category: Category) {
        val id = category.id
        dao.delete(id)
        // reformat categories
        val categories = dao.getAll().first().sortedBy { it.position }
        categories.forEachIndexed { index, it ->
            setPosition(it, index)
        }

    }

}