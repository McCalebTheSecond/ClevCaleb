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

    const val PREVIEW_ERROR = "Error"

    /** Max digit count the calculator display can show without losing correctness. */
    private const val MAX_DISPLAY_DIGITS = 15

    /** Largest integer exactly representable as a [Double]. */
    private const val MAX_EXACT_INTEGER = 9_007_199_254_740_992L

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

    /** Maps a cursor index in [formatExpression] output back to the ungrouped [raw] index. */
    fun displayOffsetToRaw(raw: String, displayOffset: Int): Int {
        val display = formatExpression(raw)
        if (displayOffset <= 0) return 0
        if (displayOffset >= display.length) return raw.length

        var rawIdx = 0
        var displayIdx = 0
        while (displayIdx < displayOffset && rawIdx < raw.length) {
            if (display[displayIdx] == ',') {
                displayIdx++
            } else if (display[displayIdx] == raw[rawIdx]) {
                displayIdx++
                rawIdx++
            } else {
                break
            }
        }
        return rawIdx
    }

    /** Maps an ungrouped [raw] index to a cursor index in [formatExpression] output. */
    fun rawOffsetToDisplay(raw: String, rawOffset: Int): Int {
        val display = formatExpression(raw)
        if (rawOffset <= 0) return 0
        if (rawOffset >= raw.length) return display.length

        var rawIdx = 0
        var displayIdx = 0
        while (rawIdx < rawOffset && displayIdx < display.length) {
            if (display[displayIdx] == ',') {
                displayIdx++
            } else if (display[displayIdx] == raw[rawIdx]) {
                displayIdx++
                rawIdx++
            } else {
                break
            }
        }
        return displayIdx
    }

    /** Whether a numeric result can be shown accurately in the calculator display. */
    fun fitsDisplay(value: Double, maxDecimals: Int = 10): Boolean {
        if (!value.isFinite()) return false
        val formatted = calculator(value, maxDecimals)
        if (formatted == PREVIEW_ERROR) return false
        if (formatted.count { it.isDigit() } > MAX_DISPLAY_DIGITS) return false
        if (abs(value - value.roundToLong().toDouble()) < 1e-9) {
            if (abs(value.roundToLong()) > MAX_EXACT_INTEGER) return false
        }
        return true
    }

    /** Live-preview text for a computed result, or [PREVIEW_ERROR] when it cannot be shown. */
    fun previewResult(value: Double, maxDecimals: Int = 10): String {
        if (!fitsDisplay(value, maxDecimals)) return PREVIEW_ERROR
        return calculator(value, maxDecimals)
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
