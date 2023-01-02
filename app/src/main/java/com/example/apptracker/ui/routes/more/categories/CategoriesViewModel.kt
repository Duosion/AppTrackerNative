package com.example.apptracker.ui.routes.more.categories

import androidx.compose.runtime.Stable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apptracker.util.data.AppDatabase
import com.example.apptracker.util.data.categories.CategoriesRepository
import com.example.apptracker.util.data.categories.Category
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Stable
class CategoriesViewModel (
    database: AppDatabase
) : ViewModel() {

    private val categoriesDao = database.categoriesDao()
    private val categoriesRepository = CategoriesRepository(categoriesDao)

    private val _screenState = MutableStateFlow(CategoriesScreenState())
    val state: StateFlow<CategoriesScreenState> = _screenState.asStateFlow()

    init {
        refresh()
    }

    private fun refresh() = viewModelScope.launch {
        //_screenState.value = CategoriesScreenState(isLoading = true)
        withContext(Dispatchers.IO) {
            _screenState.value = CategoriesScreenState(
                categories = categoriesRepository.getCategories()
            )
        }
    }

    fun addCategory(name: String) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            categoriesRepository.addCategory(Category(
                name = name,
                position = categoriesDao.getAll().first().count()
            ))
        }
    }

    fun setPosition(category: Category, newPosition: Int) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            // find category in existing position
            val existing = _screenState.value.categories.first().find { it.position == newPosition }
            existing?.let { categoriesRepository.setPosition(existing, category.position) }
            categoriesRepository.setPosition(category,newPosition)
        }
    }

    fun setName(category: Category, newName: String) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            categoriesRepository.setName(category, newName)
        }
    }

    fun delete(category: Category) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            categoriesRepository.deleteCategory(category)
        }
    }

}