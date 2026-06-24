package com.clevcaleb.app

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class CalculatorTest {

    @Test
    fun addsTwoNumbers() {
        assertEquals(5.0, Calculator.add(2.0, 3.0), 0.0)
    }

    @Test
    fun subtractsTwoNumbers() {
        assertEquals(1.0, Calculator.subtract(3.0, 2.0), 0.0)
    }

    @Test
    fun multipliesTwoNumbers() {
        assertEquals(6.0, Calculator.multiply(2.0, 3.0), 0.0)
    }

    @Test
    fun dividesTwoNumbers() {
        assertEquals(2.0, Calculator.divide(6.0, 3.0), 0.0)
    }

    @Test
    fun evaluatesUsingOperatorSymbols() {
        assertEquals(7.0, Calculator.evaluate(1.0, '+', 6.0), 0.0)
        assertEquals(12.0, Calculator.evaluate(3.0, '\u00D7', 4.0), 0.0)
        assertEquals(2.5, Calculator.evaluate(5.0, '\u00F7', 2.0), 0.0)
    }

    @Test
    fun divideByZeroThrows() {
        assertThrows(IllegalArgumentException::class.java) {
            Calculator.divide(1.0, 0.0)
        }
    }
}
