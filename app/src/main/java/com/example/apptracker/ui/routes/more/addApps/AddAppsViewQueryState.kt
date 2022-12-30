package com.example.apptracker.ui.routes.more.addApps

data class AddAppsViewQueryState(
    val query: String = "",
    val sortMode: SortFunction = SortFunction.Name
)
