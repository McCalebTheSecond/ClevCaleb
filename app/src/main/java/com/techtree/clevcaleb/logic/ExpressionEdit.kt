package com.techtree.clevcaleb.logic

object ExpressionEdit {
    /** Inserts `(` or `)` depending on whether an open parenthesis is waiting to close before [cursorRaw]. */
    fun parenthesisToken(raw: String, cursorRaw: Int): String {
        val end = cursorRaw.coerceIn(0, raw.length)
        var balance = 0
        for (i in 0 until end) {
            when (raw[i]) {
                '(' -> balance++
                ')' -> balance--
            }
        }
        return if (balance > 0) ")" else "("
    }
}
