package com.techtree.clevcaleb.logic

import net.objecthunter.exp4j.ExpressionBuilder
import kotlin.math.PI

object MathEngine {
    enum class AngleMode { DEG, RAD }

    private val sinDegPattern = Regex("""\bsin\(""")
    private val cosDegPattern = Regex("""\bcos\(""")
    private val tanDegPattern = Regex("""\btan\(""")

    /** A + B% or A - B% where B% means B percent of A (ClevCalc / GNOME style). */
    private val addSubPercentPattern = Regex("""([\d.]+)([+\-])([\d.]+)%""")

    /** A / B% divides by the percentage value, not the raw number. */
    private val divPercentPattern = Regex("""([\d.]+)/([\d.]+)%""")

    /** Remaining N% becomes N/100 (e.g. 5% -> 0.05, or 100*5% -> 100*5/100). */
    private val barePercentPattern = Regex("""([\d.]+)%""")

    /** Trailing operators that leave an expression incomplete for evaluation. */
    private val trailingOperatorPattern = Regex("""[+\-*/^]$""")

    private val sinDegFn = object : net.objecthunter.exp4j.function.Function("sinDeg", 1) {
        override fun apply(vararg args: Double) = kotlin.math.sin(Math.toRadians(args[0]))
    }
    private val cosDegFn = object : net.objecthunter.exp4j.function.Function("cosDeg", 1) {
        override fun apply(vararg args: Double) = kotlin.math.cos(Math.toRadians(args[0]))
    }
    private val tanDegFn = object : net.objecthunter.exp4j.function.Function("tanDeg", 1) {
        override fun apply(vararg args: Double) = kotlin.math.tan(Math.toRadians(args[0]))
    }

    internal fun stripTrailingOperators(expression: String): String {
        var result = expression.trim()
        while (result.isNotEmpty() && trailingOperatorPattern.containsMatchIn(result)) {
            result = result.dropLast(1).trimEnd()
        }
        return result
    }

    internal fun preprocessPercentages(expression: String): String {
        var result = expression

        result = addSubPercentPattern.replace(result) { match ->
            val left = match.groupValues[1]
            val op = match.groupValues[2]
            val pct = match.groupValues[3]
            if (op == "+") "$left+$left*$pct/100" else "$left-$left*$pct/100"
        }

        result = divPercentPattern.replace(result) { match ->
            val a = match.groupValues[1]
            val b = match.groupValues[2]
            "$a*100/$b"
        }

        result = barePercentPattern.replace(result, "$1/100")

        return result
    }

    fun evaluate(expression: String, angleMode: AngleMode = AngleMode.DEG): Double? {
        val cleaned = expression
            .replace("×", "*")
            .replace("÷", "/")
            .replace("−", "-")
            .replace("π", PI.toString())
            .trim()
        if (cleaned.isEmpty()) return null

        val stripped = stripTrailingOperators(cleaned)
        if (stripped.isEmpty()) return null

        val withPercents = preprocessPercentages(stripped)

        val prepared = if (angleMode == AngleMode.DEG) {
            withPercents
                .replace(sinDegPattern, "sinDeg(")
                .replace(cosDegPattern, "cosDeg(")
                .replace(tanDegPattern, "tanDeg(")
        } else {
            withPercents
        }

        return try {
            ExpressionBuilder(prepared)
                .functions(sinDegFn, cosDegFn, tanDegFn)
                .build()
                .evaluate()
        } catch (_: Exception) {
            null
        }
    }
}
