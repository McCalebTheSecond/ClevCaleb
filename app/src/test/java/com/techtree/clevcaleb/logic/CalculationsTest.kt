package com.techtree.clevcaleb.logic

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class CalculationsTest {
    @Test
    fun unitConverterMilesToKm() {
        val km = UnitConverterLogic.convert(1.0, "mi", "km", UnitCategory.LENGTH)
        assertNotNull(km)
        assertEquals(1.609344, km!!, 0.001)
    }

    @Test
    fun celsiusToFahrenheit() {
        val f = UnitConverterLogic.convert(0.0, "c", "f", UnitCategory.TEMPERATURE)
        assertNotNull(f)
        assertEquals(32.0, f!!, 0.01)
    }

    @Test
    fun loanPayment() {
        val result = FinanceCalculations.loanPayment(10000.0, 5.0, 5.0)
        assertNotNull(result)
        val (monthly, total, interest) = result!!
        assertEquals(188.71, monthly, 0.1)
        assertEquals(total - 10000.0, interest, 0.1)
    }

    @Test
    fun loanPaymentZeroTermReturnsNull() {
        assertEquals(null, FinanceCalculations.loanPayment(10000.0, 5.0, 0.0))
    }

    @Test
    fun mathEngineBasic() {
        val result = MathEngine.evaluate("2+3*4")
        assertNotNull(result)
        assertEquals(14.0, result!!, 0.0001)
    }

    @Test
    fun mathEnginePercentOfMultiplication() {
        val result = MathEngine.evaluate("100×5%")
        assertNotNull(result)
        assertEquals(5.0, result!!, 0.0001)
    }

    @Test
    fun mathEnginePercentWithMultiplyOperator() {
        val result = MathEngine.evaluate("100*5%")
        assertNotNull(result)
        assertEquals(5.0, result!!, 0.0001)
    }

    @Test
    fun mathEnginePercentAddition() {
        val result = MathEngine.evaluate("200+10%")
        assertNotNull(result)
        assertEquals(220.0, result!!, 0.0001)
    }

    @Test
    fun mathEnginePercentSubtraction() {
        val result = MathEngine.evaluate("200-10%")
        assertNotNull(result)
        assertEquals(180.0, result!!, 0.0001)
    }

    @Test
    fun mathEngineStandalonePercent() {
        val result = MathEngine.evaluate("50%")
        assertNotNull(result)
        assertEquals(0.5, result!!, 0.0001)
    }

    @Test
    fun mathEnginePercentDivision() {
        val result = MathEngine.evaluate("100÷5%")
        assertNotNull(result)
        assertEquals(2000.0, result!!, 0.0001)
    }

    @Test
    fun mathEnginePercentChainedMultiplication() {
        val result = MathEngine.evaluate("100×5%×2")
        assertNotNull(result)
        assertEquals(10.0, result!!, 0.0001)
    }

    @Test
    fun mathEngineLegacyPercentSyntax() {
        // Old builds appended "/100*" instead of "%"
        val result = MathEngine.evaluate("100×5/100*")
        assertNotNull(result)
        assertEquals(5.0, result!!, 0.0001)
    }

    @Test
    fun mathEnginePreprocessPercentages() {
        assertEquals("100*5/100", MathEngine.preprocessPercentages("100*5%"))
        assertEquals("200+200*10/100", MathEngine.preprocessPercentages("200+10%"))
        assertEquals("50/100", MathEngine.preprocessPercentages("50%"))
    }

    @Test
    fun mathEngineStripTrailingOperators() {
        assertEquals("100*5/100", MathEngine.stripTrailingOperators("100*5/100*"))
        assertEquals("100*5%", MathEngine.stripTrailingOperators("100*5%"))
    }

    @Test
    fun currencyConvert() {
        val rates = mapOf("USD" to 1.0, "EUR" to 0.5)
        assertEquals(50.0, CurrencyRepository.convert(100.0, "USD", "EUR", rates), 0.01)
    }

    @Test
    fun calculatorFormattingStripsTrailingZeros() {
        assertEquals("3", Formatters.calculator(3.0))
        assertEquals("3", Formatters.calculator(1.0 + 2.0))
        assertEquals("0.5", Formatters.calculator(0.5))
        assertEquals("3.14", Formatters.calculator(3.14))
    }
}
