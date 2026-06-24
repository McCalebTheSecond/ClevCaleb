package com.techtree.clevcaleb.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.techtree.clevcaleb.model.CalculatorRoute
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "clevcaleb_prefs")

object PrefKeys {
    val STARTUP_CALCULATOR = stringPreferencesKey("startup_calculator")
    val FAVORITES = stringSetPreferencesKey("favorites")
    val VISIBLE_CALCULATORS = stringSetPreferencesKey("visible_calculators")
    val BUTTON_VIBRATION = booleanPreferencesKey("button_vibration")
    val KEEP_SCREEN_ON = booleanPreferencesKey("keep_screen_on")
    val KEEP_CALC_RECORD = booleanPreferencesKey("keep_calc_record")
    val OPEN_LIST_AT_STARTUP = booleanPreferencesKey("open_list_at_startup")
    val NUMBER_FORMAT = stringPreferencesKey("number_format")
    val LAST_EXPRESSION = stringPreferencesKey("last_expression")
    val HISTORY = stringPreferencesKey("history")
}

class AppPreferences(private val context: Context) {
    val startupCalculator: Flow<CalculatorRoute> = context.dataStore.data.map { prefs ->
        val id = prefs[PrefKeys.STARTUP_CALCULATOR] ?: CalculatorRoute.Main.route
        CalculatorRoute.fromRoute(id) ?: CalculatorRoute.Main
    }

    val favorites: Flow<List<CalculatorRoute>> = context.dataStore.data.map { prefs ->
        val ids = prefs[PrefKeys.FAVORITES] ?: defaultFavorites()
        ids.mapNotNull { CalculatorRoute.fromRoute(it) }
    }

    val visibleCalculators: Flow<List<CalculatorRoute>> = context.dataStore.data.map { prefs ->
        val ids = prefs[PrefKeys.VISIBLE_CALCULATORS] ?: CalculatorRoute.entries.map { it.route }.toSet()
        CalculatorRoute.entries.filter { it.route in ids }
    }

    val buttonVibration: Flow<Boolean> = context.dataStore.data.map { it[PrefKeys.BUTTON_VIBRATION] ?: true }
    val keepScreenOn: Flow<Boolean> = context.dataStore.data.map { it[PrefKeys.KEEP_SCREEN_ON] ?: true }
    val keepCalcRecord: Flow<Boolean> = context.dataStore.data.map { it[PrefKeys.KEEP_CALC_RECORD] ?: true }
    val openListAtStartup: Flow<Boolean> = context.dataStore.data.map { it[PrefKeys.OPEN_LIST_AT_STARTUP] ?: false }
    val numberFormat: Flow<String> = context.dataStore.data.map { it[PrefKeys.NUMBER_FORMAT] ?: "default" }
    val lastExpression: Flow<String> = context.dataStore.data.map { it[PrefKeys.LAST_EXPRESSION] ?: "" }
    val history: Flow<List<String>> = context.dataStore.data.map { prefs ->
        prefs[PrefKeys.HISTORY]?.split("\n")?.filter { it.isNotBlank() } ?: emptyList()
    }

    suspend fun setStartupCalculator(route: CalculatorRoute) = edit(PrefKeys.STARTUP_CALCULATOR, route.route)
    suspend fun setFavorites(routes: List<CalculatorRoute>) = editSet(PrefKeys.FAVORITES, routes.map { it.route }.toSet())
    suspend fun setVisibleCalculators(routes: List<CalculatorRoute>) = editSet(PrefKeys.VISIBLE_CALCULATORS, routes.map { it.route }.toSet())
    suspend fun setButtonVibration(enabled: Boolean) = edit(PrefKeys.BUTTON_VIBRATION, enabled)
    suspend fun setKeepScreenOn(enabled: Boolean) = edit(PrefKeys.KEEP_SCREEN_ON, enabled)
    suspend fun setKeepCalcRecord(enabled: Boolean) = edit(PrefKeys.KEEP_CALC_RECORD, enabled)
    suspend fun setOpenListAtStartup(enabled: Boolean) = edit(PrefKeys.OPEN_LIST_AT_STARTUP, enabled)
    suspend fun setNumberFormat(format: String) = edit(PrefKeys.NUMBER_FORMAT, format)
    suspend fun setLastExpression(expr: String) = edit(PrefKeys.LAST_EXPRESSION, expr)
    suspend fun setHistory(items: List<String>) = edit(PrefKeys.HISTORY, items.joinToString("\n"))

    private suspend fun <T> edit(key: Preferences.Key<T>, value: T) {
        context.dataStore.edit { it[key] = value }
    }

    private suspend fun editSet(key: Preferences.Key<Set<String>>, value: Set<String>) {
        context.dataStore.edit { it[key] = value }
    }

    private fun defaultFavorites(): Set<String> = setOf(
        CalculatorRoute.Main.route,
        CalculatorRoute.FuelCost.route,
        CalculatorRoute.Percentage.route,
    )
}
