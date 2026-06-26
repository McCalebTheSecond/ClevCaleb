package com.techtree.clevcaleb.logic

object ExpressionEdit {
    /** Inserts `(` or `)` depending on whether an open parenthesis is waiting to close before [cursorRaw]. */
    fun parenthesisToken(raw: String, cursorRaw: Int): String {
        val before = raw.substring(0, cursorRaw.coerceIn(0, raw.length))
        val opens = before.count { it == '(' }
        val closes = before.count { it == ')' }
        return if (opens > closes) ")" else "("
    }
}
