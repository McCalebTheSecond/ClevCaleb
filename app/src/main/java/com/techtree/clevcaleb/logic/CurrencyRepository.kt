package com.techtree.clevcaleb.logic

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

data class Currency(val code: String, val name: String, val symbol: String)

object CurrencyRepository {
    val currencies = listOf(
        Currency("USD", "US Dollar", "$"),
        Currency("EUR", "Euro", "€"),
        Currency("GBP", "British Pound", "£"),
        Currency("JPY", "Japanese Yen", "¥"),
        Currency("CNY", "Chinese Yuan", "¥"),
        Currency("CAD", "Canadian Dollar", "C$"),
        Currency("AUD", "Australian Dollar", "A$"),
        Currency("CHF", "Swiss Franc", "CHF"),
        Currency("INR", "Indian Rupee", "₹"),
        Currency("KRW", "South Korean Won", "₩"),
        Currency("MXN", "Mexican Peso", "MX$"),
        Currency("BRL", "Brazilian Real", "R$"),
        Currency("SEK", "Swedish Krona", "kr"),
        Currency("NOK", "Norwegian Krone", "kr"),
        Currency("PLN", "Polish Zloty", "zł"),
        Currency("TRY", "Turkish Lira", "₺"),
        Currency("SGD", "Singapore Dollar", "S$"),
        Currency("HKD", "Hong Kong Dollar", "HK$"),
        Currency("NZD", "New Zealand Dollar", "NZ$"),
        Currency("THB", "Thai Baht", "฿"),
        Currency("ZAR", "South African Rand", "R"),
        Currency("AED", "UAE Dirham", "د.إ"),
        Currency("ILS", "Israeli Shekel", "₪"),
        Currency("PHP", "Philippine Peso", "₱"),
        Currency("MYR", "Malaysian Ringgit", "RM"),
        Currency("TWD", "Taiwan Dollar", "NT$"),
    )

    private val fallbackRates = mapOf(
        "USD" to 1.0, "EUR" to 0.92, "GBP" to 0.79, "JPY" to 149.5, "CNY" to 7.24,
        "CAD" to 1.36, "AUD" to 1.53, "CHF" to 0.88, "INR" to 83.1, "KRW" to 1320.0,
        "MXN" to 17.1, "BRL" to 4.97, "SEK" to 10.5, "NOK" to 10.7, "PLN" to 3.98,
        "TRY" to 32.1, "SGD" to 1.34, "HKD" to 7.82, "NZD" to 1.64, "THB" to 35.8,
        "ZAR" to 18.6, "AED" to 3.67, "ILS" to 3.68, "PHP" to 56.2, "MYR" to 4.72, "TWD" to 31.8,
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
                ratesObj.keys().forEach { key -> rates[key] = ratesObj.getDouble(key) }
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
