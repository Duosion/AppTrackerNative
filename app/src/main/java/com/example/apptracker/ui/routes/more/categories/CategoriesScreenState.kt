package com.example.apptracker.ui.routes.more.categories

import com.example.apptracker.util.data.categories.Category
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

data class CategoriesScreenState(
    val categories: Flow<List<Category>> = MutableStateFlow(listOf()),
    val isLoading: Boolean = false
)
