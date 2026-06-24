package com.techtree.clevcaleb.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.techtree.clevcaleb.data.AppPreferences
import com.techtree.clevcaleb.model.CalculatorRoute
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AppViewModel(private val prefs: AppPreferences) : ViewModel() {
    private var persistExpressionJob: Job? = null

    val favorites = prefs.favorites.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    val visibleCalculators = prefs.visibleCalculators.stateIn(viewModelScope, SharingStarted.Eagerly, CalculatorRoute.entries)
    val startupCalculator = prefs.startupCalculator.stateIn(viewModelScope, SharingStarted.Eagerly, CalculatorRoute.Main)
    val buttonVibration = prefs.buttonVibration.stateIn(viewModelScope, SharingStarted.Eagerly, true)
    val keepScreenOn = prefs.keepScreenOn.stateIn(viewModelScope, SharingStarted.Eagerly, true)
    val keepCalcRecord = prefs.keepCalcRecord.stateIn(viewModelScope, SharingStarted.Eagerly, true)
    val openListAtStartup = prefs.openListAtStartup.stateIn(viewModelScope, SharingStarted.Eagerly, false)
    val lastExpression = prefs.lastExpression.stateIn(viewModelScope, SharingStarted.Eagerly, "")
    val history = prefs.history.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun setStartupCalculator(route: CalculatorRoute) = viewModelScope.launch { prefs.setStartupCalculator(route) }
    fun setFavorites(routes: List<CalculatorRoute>) = viewModelScope.launch { prefs.setFavorites(routes) }
    fun setVisibleCalculators(routes: List<CalculatorRoute>) = viewModelScope.launch { prefs.setVisibleCalculators(routes) }
    fun setButtonVibration(enabled: Boolean) = viewModelScope.launch { prefs.setButtonVibration(enabled) }
    fun setKeepScreenOn(enabled: Boolean) = viewModelScope.launch { prefs.setKeepScreenOn(enabled) }
    fun setKeepCalcRecord(enabled: Boolean) = viewModelScope.launch { prefs.setKeepCalcRecord(enabled) }
    fun setOpenListAtStartup(enabled: Boolean) = viewModelScope.launch { prefs.setOpenListAtStartup(enabled) }
    fun setLastExpression(expr: String, immediate: Boolean = false) {
        persistExpressionJob?.cancel()
        if (immediate) {
            viewModelScope.launch { prefs.setLastExpression(expr) }
        } else {
            persistExpressionJob = viewModelScope.launch {
                delay(500)
                prefs.setLastExpression(expr)
            }
        }
    }

    fun flushLastExpression(expr: String) = setLastExpression(expr, immediate = true)
    fun setHistory(items: List<String>) = viewModelScope.launch { prefs.setHistory(items) }
    fun addHistory(entry: String) = viewModelScope.launch {
        val next = listOf(entry) + history.value.filter { it != entry }
        prefs.setHistory(next.take(50))
    }
}

class AppViewModelFactory(private val prefs: AppPreferences) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = AppViewModel(prefs) as T
}

@Composable
fun rememberAppViewModel(prefs: AppPreferences): AppViewModel {
    return viewModel(factory = AppViewModelFactory(prefs))
}
