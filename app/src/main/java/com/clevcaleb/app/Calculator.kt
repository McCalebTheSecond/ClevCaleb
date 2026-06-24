package com.clevcaleb.app

/**
 * Pure arithmetic logic for the calculator, kept free of Android types so it can
 * be exercised by fast JVM unit tests.
 */
object Calculator {

    fun add(a: Double, b: Double): Double = a + b

    fun subtract(a: Double, b: Double): Double = a - b

    fun multiply(a: Double, b: Double): Double = a * b

    fun divide(a: Double, b: Double): Double {
        require(b != 0.0) { "Cannot divide by zero" }
        return a / b
    }

    fun evaluate(a: Double, op: Char, b: Double): Double = when (op) {
        '+' -> add(a, b)
        '-', '\u2212' -> subtract(a, b)
        '*', '\u00D7' -> multiply(a, b)
        '/', '\u00F7' -> divide(a, b)
        else -> throw IllegalArgumentException("Unknown operator: $op")
    }
}
