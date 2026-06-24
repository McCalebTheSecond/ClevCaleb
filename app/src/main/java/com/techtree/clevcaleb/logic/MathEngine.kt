package com.techtree.clevcaleb.logic

import net.objecthunter.exp4j.ExpressionBuilder
import kotlin.math.PI

object MathEngine {
    enum class AngleMode { DEG, RAD }

    private val sinDegPattern = Regex("""\bsin\(""")
    private val cosDegPattern = Regex("""\bcos\(""")
    private val tanDegPattern = Regex("""\btan\(""")

    private val sinDegFn = object : net.objecthunter.exp4j.function.Function("sinDeg", 1) {
        override fun apply(vararg args: Double) = kotlin.math.sin(Math.toRadians(args[0]))
    }
    private val cosDegFn = object : net.objecthunter.exp4j.function.Function("cosDeg", 1) {
        override fun apply(vararg args: Double) = kotlin.math.cos(Math.toRadians(args[0]))
    }
    private val tanDegFn = object : net.objecthunter.exp4j.function.Function("tanDeg", 1) {
        override fun apply(vararg args: Double) = kotlin.math.tan(Math.toRadians(args[0]))
    }

    fun evaluate(expression: String, angleMode: AngleMode = AngleMode.DEG): Double? {
        val cleaned = expression
            .replace("×", "*")
            .replace("÷", "/")
            .replace("−", "-")
            .replace("π", PI.toString())
            .trim()
        if (cleaned.isEmpty()) return null

        val prepared = if (angleMode == AngleMode.DEG) {
            cleaned
                .replace(sinDegPattern, "sinDeg(")
                .replace(cosDegPattern, "cosDeg(")
                .replace(tanDegPattern, "tanDeg(")
        } else {
            cleaned
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
