package com.techtree.clevcaleb.logic

import java.util.Locale
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToLong

/** US English number and currency formatting. */
object Formatters {
    private val locale = Locale.US

    fun number(value: Double, decimals: Int = 2): String =
        String.format(locale, "%,.${decimals}f", value)

    fun currency(value: Double): String = "$${number(value)}"

    fun integer(value: Long): String = String.format(locale, "%,d", value)

    /**
     * Calculator display: no thousands separators, no pointless trailing zeros.
     * `1+2` → `3`, not `3.000000`.
     */
    fun calculator(value: Double, maxDecimals: Int = 10): String {
        if (!value.isFinite()) return "Error"
        val factor = 10.0.pow(maxDecimals.coerceIn(0, 15).toDouble())
        val rounded = (value * factor).roundToLong() / factor
        if (abs(rounded - rounded.toLong()) < 1e-12 && abs(rounded) < 1e15) {
            return rounded.toLong().toString()
        }
        return String.format(locale, "%.${maxDecimals.coerceIn(0, 15)}f", rounded)
            .trimEnd('0')
            .trimEnd('.')
    }
}
