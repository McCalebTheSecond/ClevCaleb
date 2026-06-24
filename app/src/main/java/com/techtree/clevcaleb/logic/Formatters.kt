package com.techtree.clevcaleb.logic

import java.util.Locale

/** US English number and currency formatting. */
object Formatters {
    private val locale = Locale.US

    fun number(value: Double, decimals: Int = 2): String =
        String.format(locale, "%,.${decimals}f", value)

    fun currency(value: Double): String = "$${number(value)}"

    fun integer(value: Long): String = String.format(locale, "%,d", value)
}
