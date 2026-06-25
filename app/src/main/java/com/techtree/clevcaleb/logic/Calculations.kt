package com.techtree.clevcaleb.logic

import kotlin.math.pow

object FinanceCalculations {
    fun loanPayment(principal: Double, annualRatePercent: Double, years: Double): Triple<Double, Double, Double>? {
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
    ): Triple<Double, Double, Double> {
        val months = (years * 12).toInt()
        val r = annualRatePercent / 100 / 12 * (1 - taxOnInterestPercent / 100)
        var balance = initial
        for (_i in 0 until months) {
            balance = balance * (1 + r) + monthly
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
    ): Triple<Double, Double, Double> {
        val base = if (excludeTax) bill - tax else bill
        val tip = base * tipPercent / 100
        val total = bill + tip
        return Triple(tip, total, total / people)
    }
}

object HealthCalculations {
    fun bmi(weightKg: Double, heightCm: Double): Double {
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
