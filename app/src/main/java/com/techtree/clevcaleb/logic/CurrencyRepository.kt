package com.techtree.clevcaleb.logic

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class Currency(val code: String, val name: String, val symbol: String)

/** USD-centric currencies commonly used in the US (English, US-only build). */
object CurrencyRepository {
    val currencies = listOf(
        Currency("USD", "US Dollar", "$"),
        Currency("CAD", "Canadian Dollar", "CA$"),
        Currency("MXN", "Mexican Peso", "MX$"),
        Currency("EUR", "Euro", "€"),
        Currency("GBP", "British Pound", "£"),
        Currency("JPY", "Japanese Yen", "¥"),
        Currency("AUD", "Australian Dollar", "A$"),
        Currency("CHF", "Swiss Franc", "CHF"),
    )

    val dropdownOptions: List<Pair<String, String>> by lazy {
        currencies.map { it.code to "${it.code} — ${it.name}" }
    }

    private val fallbackRates = mapOf(
        "USD" to 1.0,
        "CAD" to 1.36,
        "MXN" to 17.1,
        "EUR" to 0.92,
        "GBP" to 0.79,
        "JPY" to 149.5,
        "AUD" to 1.53,
        "CHF" to 0.88,
    )

    private val currencyCodes = currencies.map { it.code }.toSet()

    private val client = OkHttpClient.Builder()
        .callTimeout(10, TimeUnit.SECONDS)
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .build()

    @Volatile
    private var cachedRates: Map<String, Double>? = null

    @Volatile
    private var cacheFetchedAtMs: Long = 0

    @Volatile
    private var lastFetchFailedAtMs: Long = 0

    private const val CACHE_TTL_MS = 3_600_000L
    private const val FAILURE_BACKOFF_MS = 300_000L

    private val fetchMutex = Mutex()

    suspend fun fetchRates(): Map<String, Double> = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        cachedRates?.let { cached ->
            if (now - cacheFetchedAtMs < CACHE_TTL_MS) return@withContext cached
        }
        if (now - lastFetchFailedAtMs < FAILURE_BACKOFF_MS) {
            return@withContext cachedRates ?: fallbackRates
        }
        fetchMutex.withLock {
            val lockedNow = System.currentTimeMillis()
            cachedRates?.let { cached ->
                if (lockedNow - cacheFetchedAtMs < CACHE_TTL_MS) return@withLock cached
            }
            if (lockedNow - lastFetchFailedAtMs < FAILURE_BACKOFF_MS) {
                return@withLock cachedRates ?: fallbackRates
            }
            fetchRatesFromNetwork(lockedNow)
        }
    }

    private fun fetchRatesFromNetwork(now: Long): Map<String, Double> = try {
        val request = Request.Builder()
            .url("https://api.frankfurter.app/latest?from=USD")
            .build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                lastFetchFailedAtMs = now
                return cachedRates ?: fallbackRates
            }
            val json = JSONObject(response.body?.string() ?: run {
                lastFetchFailedAtMs = now
                return cachedRates ?: fallbackRates
            })
            val rates = mutableMapOf("USD" to 1.0)
            val ratesObj = json.getJSONObject("rates")
            ratesObj.keys().forEach { key ->
                if (key in currencyCodes) {
                    rates[key] = ratesObj.getDouble(key)
                }
            }
            fallbackRates.forEach { (code, rate) ->
                if (code !in rates) rates[code] = rate
            }
            cachedRates = rates
            cacheFetchedAtMs = now
            lastFetchFailedAtMs = 0
            rates
        }
    } catch (_: Exception) {
        lastFetchFailedAtMs = now
        cachedRates ?: fallbackRates
    }

    fun convert(amount: Double, from: String, to: String, rates: Map<String, Double>): Double {
        val fromRate = rates[from] ?: fallbackRates[from] ?: 1.0
        val toRate = rates[to] ?: fallbackRates[to] ?: 1.0
        return amount / fromRate * toRate
    }
}
