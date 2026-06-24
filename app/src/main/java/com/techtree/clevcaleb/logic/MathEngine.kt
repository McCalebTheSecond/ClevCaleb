package com.techtree.clevcaleb.logic

import net.objecthunter.exp4j.ExpressionBuilder
import kotlin.math.PI

object MathEngine {
    enum class AngleMode { DEG, RAD }

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
                .replace(Regex("""\bsin\("""), "sinDeg(")
                .replace(Regex("""\bcos\("""), "cosDeg(")
                .replace(Regex("""\btan\("""), "tanDeg(")
        } else {
            cleaned
        }

        return try {
            ExpressionBuilder(prepared)
                .functions(
                    object : net.objecthunter.exp4j.function.Function("sinDeg", 1) {
                        override fun apply(vararg args: Double) = kotlin.math.sin(Math.toRadians(args[0]))
                    },
                    object : net.objecthunter.exp4j.function.Function("cosDeg", 1) {
                        override fun apply(vararg args: Double) = kotlin.math.cos(Math.toRadians(args[0]))
                    },
                    object : net.objecthunter.exp4j.function.Function("tanDeg", 1) {
                        override fun apply(vararg args: Double) = kotlin.math.tan(Math.toRadians(args[0]))
                    },
                )
                .build()
                .evaluate()
        } catch (_: Exception) {
            null
        }
    }
}
