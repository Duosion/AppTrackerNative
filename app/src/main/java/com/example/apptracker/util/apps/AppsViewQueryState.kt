package com.example.apptracker.util.apps

data class AppsViewQueryState(
    val query: String = "",
    val sortMode: SortFunction = SortFunction.Name
)
