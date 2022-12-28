package com.example.apptracker.ui.routes.more.categories

import com.example.apptracker.util.data.categories.Category

data class CategoryDialogState(
    val enabled: Boolean = false,
    val category: Category? = null
)
