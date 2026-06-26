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
    fun savingsBalance() {
        val result = FinanceCalculations.savingsBalance(1000.0, 200.0, 4.5, 10.0, 0.0)
        assertNotNull(result)
        val (balance, contributed, interest) = result!!
        assertEquals(1000.0 + 200.0 * 120, contributed, 0.01)
        assertEquals(balance - contributed, interest, 0.01)
        assertEquals(31_806.61, balance, 1.0)
    }

    @Test
    fun savingsBalanceZeroYearsReturnsNull() {
        assertEquals(null, FinanceCalculations.savingsBalance(1000.0, 200.0, 4.5, 0.0, 0.0))
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

    @Test
    fun calculatorFormattingUsesThousandsSeparators() {
        assertEquals("3,000", Formatters.calculator(3000.0))
        assertEquals("1,234,567", Formatters.calculator(1_234_567.0))
    }

    @Test
    fun formatExpressionAddsThousandsSeparators() {
        assertEquals("1,000+2,000", Formatters.formatExpression("1000+2000"))
        assertEquals("1,234.56", Formatters.formatExpression("1234.56"))
        assertEquals("12,345,678", Formatters.formatExpression("12345678"))
    }

    @Test
    fun mathEngineEvaluatesFormattedExpression() {
        val result = MathEngine.evaluate("1,000+2,000")
        assertNotNull(result)
        assertEquals(3000.0, result!!, 0.0001)
    }

    @Test
    fun mathEngineLongAdditionChain() {
        val expr = (1..200).joinToString("+")
        val result = MathEngine.evaluate(expr)
        assertNotNull(result)
        assertEquals(20_100.0, result!!, 0.001)
    }

    @Test
    fun mathEngineLongMultiplicationChain() {
        val expr = (1..25).joinToString("×")
        val result = MathEngine.evaluate(expr)
        assertNotNull(result)
    }

    @Test
    fun mathEnginePreviewableDetectsIncompleteExpression() {
        assertEquals(false, MathEngine.isPreviewable(""))
        assertEquals(true, MathEngine.isPreviewable("1+2+"))
        assertEquals(true, MathEngine.isPreviewable("1,000+2,000"))
    }

    @Test
    fun displayOffsetMappingRoundTripsWithGrouping() {
        val raw = "1000+2000"
        val display = Formatters.formatExpression(raw)
        assertEquals("1,000+2,000", display)
        assertEquals(0, Formatters.displayOffsetToRaw(raw, 0))
        assertEquals(4, Formatters.displayOffsetToRaw(raw, 5))
        assertEquals(5, Formatters.displayOffsetToRaw(raw, 6))
        assertEquals(raw.length, Formatters.displayOffsetToRaw(raw, display.length))
        assertEquals(5, Formatters.rawOffsetToDisplay(raw, 4))
        assertEquals(6, Formatters.rawOffsetToDisplay(raw, 5))
    }

    @Test
    fun previewResultErrorsWhenOutOfRoom() {
        val expr = (1..25).joinToString("×")
        val result = MathEngine.evaluate(expr)
        assertNotNull(result)
        assertEquals(Formatters.PREVIEW_ERROR, Formatters.previewResult(result!!, 10))
    }

    @Test
    fun previewResultShowsNormalValues() {
        assertEquals("3,000", Formatters.previewResult(3000.0, 10))
        assertEquals("3", Formatters.previewResult(3.0, 10))
    }

    @Test
    fun fitsDisplayRejectsNonFiniteValues() {
        assertEquals(false, Formatters.fitsDisplay(Double.POSITIVE_INFINITY))
        assertEquals(false, Formatters.fitsDisplay(Double.NaN))
    }

    @Test
    fun previewResultBlankForNonFinite() {
        assertEquals("", Formatters.previewResult(Double.POSITIVE_INFINITY))
        assertEquals("", Formatters.previewResult(Double.NaN))
    }

    @Test
    fun parenthesisTokenTogglesOpenAndClose() {
        assertEquals("(", ExpressionEdit.parenthesisToken("", 0))
        assertEquals(")", ExpressionEdit.parenthesisToken("(3000(", 6))
        assertEquals("(", ExpressionEdit.parenthesisToken("(3+5)", 5))
        assertEquals(")", ExpressionEdit.parenthesisToken("((1+2)", 7))
    }
}
