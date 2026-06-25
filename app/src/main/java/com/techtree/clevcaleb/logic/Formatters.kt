package com.techtree.clevcaleb.logic

import java.util.Locale
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToLong

/** US English number and currency formatting. */
object Formatters {
    private val locale = Locale.US

    /** Number literals inside calculator expressions (not function names). */
    private val expressionNumber = Regex("""(?<![a-zA-Z])\d[\d,]*(?:\.\d*)?""")

    fun stripGrouping(text: String): String = text.replace(",", "")

    fun number(value: Double, decimals: Int = 2): String =
        String.format(locale, "%,.${decimals}f", value)

    fun currency(value: Double): String = "$${number(value)}"

    fun integer(value: Long): String = String.format(locale, "%,d", value)

    /** Adds thousands separators to numeric literals in a calculator expression. */
    fun formatExpression(expression: String): String {
        val raw = stripGrouping(expression)
        return expressionNumber.replace(raw) { match ->
            formatGroupedLiteral(match.value)
        }
    }

    private fun formatGroupedLiteral(raw: String): String {
        if (raw.isEmpty()) return raw
        val dot = raw.indexOf('.')
        return if (dot < 0) {
            groupDigits(raw)
        } else {
            groupDigits(raw.substring(0, dot)) + raw.substring(dot)
        }
    }

    private fun groupDigits(digits: String): String {
        if (digits.length <= 3) return digits
        return digits.reversed().chunked(3).joinToString(",").reversed()
    }

    /**
     * Calculator display: thousands separators, no pointless trailing zeros.
     * `1000+2000` result → `3,000`, not `3.000000`.
     */
    fun calculator(value: Double, maxDecimals: Int = 10): String {
        if (!value.isFinite()) return "Error"
        val factor = 10.0.pow(maxDecimals.coerceIn(0, 15).toDouble())
        val rounded = (value * factor).roundToLong() / factor
        if (abs(rounded - rounded.toLong()) < 1e-12 && abs(rounded) < 1e15) {
            return integer(rounded.toLong())
        }
        return number(rounded, maxDecimals.coerceIn(0, 15))
            .trimEnd('0')
            .trimEnd('.')
    }
}
