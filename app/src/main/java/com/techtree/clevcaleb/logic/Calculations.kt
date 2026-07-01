package com.techtree.clevcaleb.logic

import kotlin.math.pow

object FinanceCalculations {
    fun loanPayment(principal: Double, annualRatePercent: Double, years: Double): Triple<Double, Double, Double>? {
        if (principal < 0.0 || years < 0.0) return null
        val months = years * 12
        if (months <= 0.0) return null
        val r = annualRatePercent / 100 / 12
        val monthly = if (r == 0.0) {
            principal / months
        } else {
            val factor = (1 + r).pow(months)
            principal * r * factor / (factor - 1)
        }
        val total = monthly * months
        return Triple(monthly, total, total - principal)
    }

    fun savingsBalance(
        initial: Double,
        monthly: Double,
        annualRatePercent: Double,
        years: Double,
        taxOnInterestPercent: Double,
    ): Triple<Double, Double, Double>? {
        val months = (years * 12).toInt()
        if (months <= 0) return null
        val r = annualRatePercent / 100 / 12 * (1 - taxOnInterestPercent / 100)
        val balance = if (r == 0.0) {
            initial + monthly * months
        } else {
            val factor = (1 + r).pow(months)
            initial * factor + monthly * (factor - 1) / r
        }
        val contributed = initial + monthly * months
        return Triple(balance, contributed, balance - contributed)
    }

    fun tip(
        bill: Double,
        tax: Double,
        tipPercent: Double,
        people: Int,
        excludeTax: Boolean,
    ): Triple<Double, Double, Double>? {
        if (people < 1) return null
        val base = if (excludeTax) bill - tax else bill
        if (base < 0) return null
        val tip = base * tipPercent / 100
        val total = bill + tip
        return Triple(tip, total, total / people)
    }
}

object HealthCalculations {
    fun bmi(weightKg: Double, heightCm: Double): Double? {
        if (heightCm <= 0.0) return null
        val m = heightCm / 100
        return weightKg / (m * m)
    }

    fun bmr(weightKg: Double, heightCm: Double, age: Double, male: Boolean): Double {
        return if (male) {
            10 * weightKg + 6.25 * heightCm - 5 * age + 5
        } else {
            10 * weightKg + 6.25 * heightCm - 5 * age - 161
        }
    }

    fun bmiCategory(bmi: Double): String = when {
        bmi < 18.5 -> "Underweight"
        bmi < 25 -> "Normal"
        bmi < 30 -> "Overweight"
        else -> "Obese"
    }
}
