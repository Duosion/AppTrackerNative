package com.example.apptracker.ui.routes.apps

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apptracker.ui.routes.more.categories.CategoriesScreenState
import com.example.apptracker.util.data.AppDatabase
import com.example.apptracker.util.data.categories.CategoriesRepository
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AppsViewModel(
    database: AppDatabase
) : ViewModel() {

    private val categoriesRepository = CategoriesRepository(database.categoriesDao())
    private val trackedAppDao = database.trackedAppDao()

    private val _screenState = MutableStateFlow(AppsScreenState())
    val state: StateFlow<AppsScreenState> = _screenState.asStateFlow()

    init {
        refreshCategories()
        refreshApps()
    }

    private fun refreshCategories() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            _screenState.update {
                it.copy(
                    categories = categoriesRepository.getCategories(showHidden = true)
                )
            }
        }
    }

    private fun refreshApps() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            _screenState.update {
                it.copy(
                    trackedApps = trackedAppDao.getAll()
                )
            }
        }
    }

}