package com.example.apptracker.ui.routes.more.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apptracker.util.data.AppDatabase
import com.example.apptracker.util.data.categories.CategoriesRepository
import com.example.apptracker.util.data.categories.Category
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CategoriesViewModel (
    database: AppDatabase
) : ViewModel() {

    private val categoriesRepository = CategoriesRepository(database.categoriesDao())

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

    fun addCategory(category: Category) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            categoriesRepository.addCategory(category)
            refresh()
        }
    }

    fun setPosition(category: Category, newPosition: Int) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            // find category in existing position
            val existing = _screenState.value.categories.find { it.position == newPosition }
            existing?.let { categoriesRepository.setPosition(existing, category.position) }
            categoriesRepository.setPosition(category,newPosition)
            refresh()
        }
    }

    fun setName(category: Category, newName: String) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            categoriesRepository.setName(category, newName)
            refresh()
        }
    }

    fun delete(category: Category) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            categoriesRepository.deleteCategory(category)
            refresh()
        }
    }

}