package com.example.apptracker.ui.routes.more.categories

import com.example.apptracker.util.data.categories.Category

data class CategoriesScreenState(
    val categories: List<Category> = listOf(),
    val isLoading: Boolean = false
)
