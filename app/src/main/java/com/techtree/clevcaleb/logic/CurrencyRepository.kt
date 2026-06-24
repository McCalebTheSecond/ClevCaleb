package com.techtree.clevcaleb.logic

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

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

    private val client = OkHttpClient()

    suspend fun fetchRates(): Map<String, Double> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("https://api.frankfurter.app/latest?from=USD")
                .build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@withContext fallbackRates
                val json = JSONObject(response.body?.string() ?: return@withContext fallbackRates)
                val rates = mutableMapOf("USD" to 1.0)
                val ratesObj = json.getJSONObject("rates")
                ratesObj.keys().forEach { key ->
                    if (currencies.any { it.code == key }) {
                        rates[key] = ratesObj.getDouble(key)
                    }
                }
                fallbackRates.forEach { (code, rate) ->
                    if (code !in rates) rates[code] = rate
                }
                rates
            }
        } catch (_: Exception) {
            fallbackRates
        }
    }

    fun convert(amount: Double, from: String, to: String, rates: Map<String, Double>): Double {
        val fromRate = rates[from] ?: fallbackRates[from] ?: 1.0
        val toRate = rates[to] ?: fallbackRates[to] ?: 1.0
        return amount / fromRate * toRate
    }
}
