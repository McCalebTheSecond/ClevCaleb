package com.techtree.clevcaleb.ui.calculators

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.techtree.clevcaleb.logic.Formatters
import com.techtree.clevcaleb.logic.HealthCalculations
import com.techtree.clevcaleb.ui.components.CalculatorScaffold
import com.techtree.clevcaleb.ui.components.ResultCard
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

@Composable
fun DateScreen(onBack: () -> Unit) {
    var mode by remember { mutableStateOf("diff") }
    var date1 by remember { mutableStateOf(LocalDate.now().toString()) }
    var date2 by remember { mutableStateOf(LocalDate.now().toString()) }
    var days by remember { mutableStateOf("30") }
    val fmt = remember { DateTimeFormatter.ofPattern("EEEE, MMM d, yyyy", Locale.US) }

    val results = remember(mode, date1, date2, days) {
        runCatching {
            val d1 = LocalDate.parse(date1)
            when (mode) {
                "diff" -> {
                    val d2 = LocalDate.parse(date2)
                    val diff = kotlin.math.abs(ChronoUnit.DAYS.between(d1, d2))
                    "$diff day${if (diff == 1L) "" else "s"}" to
                        "Between ${d1.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))} and ${d2.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))}"
                }
                "add" -> {
                    val n = days.toLong()
                    val r = d1.plusDays(n)
                    r.format(fmt) to "$n days after ${d1.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))}"
                }
                else -> {
                    val n = days.toLong()
                    val r = d1.minusDays(n)
                    r.format(fmt) to "$n days before ${d1.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))}"
                }
            }
        }.getOrNull()
    }

    CalculatorScaffold(title = "Date", onBack = onBack) {
        DropdownField(
            "Mode",
            listOf("diff" to "Days between two dates", "add" to "Add days", "subtract" to "Subtract days"),
            mode,
        ) { mode = it }
        DateField("Date", date1) { date1 = it }
        if (mode == "diff") DateField("End date", date2) { date2 = it }
        else NumberField("Number of days", days) { days = it }
        results?.let { (primary, secondary) ->
            ResultCard("Result", primary)
            ResultCard("Details", secondary)
        }
    }
}

@Composable
fun HealthScreen(onBack: () -> Unit) {
    var male by remember { mutableStateOf(true) }
    var age by remember { mutableStateOf("30") }
    var weight by remember { mutableStateOf("170") }
    var height by remember { mutableStateOf("70") }

    val results = remember(male, age, weight, height) {
        val w = weight.toDoubleOrNull() ?: return@remember null
        val h = height.toDoubleOrNull() ?: return@remember null
        val a = age.toDoubleOrNull() ?: return@remember null
        if (h <= 0) return@remember null
        val weightKg = w * 0.453592
        val heightCm = h * 2.54
        val bmi = HealthCalculations.bmi(weightKg, heightCm)
        Triple(
            Formatters.number(bmi, 1),
            HealthCalculations.bmiCategory(bmi),
            "${Formatters.number(HealthCalculations.bmr(weightKg, heightCm, a, male), 0)} kcal/day",
        )
    }

    CalculatorScaffold(title = "Body Metrics", onBack = onBack) {
        DropdownField("Sex", listOf(true to "Male", false to "Female"), male) { male = it }
        NumberField("Age (years)", age) { age = it }
        NumberField("Weight (lb)", weight) { weight = it }
        NumberField("Height (in)", height) { height = it }
        results?.let { (bmi, category, bmr) ->
            ResultCard("BMI", bmi)
            ResultCard("BMI category", category)
            ResultCard("BMR (Mifflin-St Jeor)", bmr)
        }
    }
}

@Composable
fun FuelCostScreen(onBack: () -> Unit) {
    var distance by remember { mutableStateOf("100") }
    var efficiency by remember { mutableStateOf("28") }
    var price by remember { mutableStateOf("3.50") }

    val result = remember(distance, efficiency, price) {
        val d = distance.toDoubleOrNull() ?: return@remember null
        val eff = efficiency.toDoubleOrNull() ?: return@remember null
        val p = price.toDoubleOrNull() ?: return@remember null
        if (eff <= 0) return@remember null
        val gallons = d / eff
        val cost = gallons * p
        Formatters.currency(cost) to "${Formatters.number(gallons)} gal"
    }

    CalculatorScaffold(title = "Fuel Cost", onBack = onBack) {
        NumberField("Distance (miles)", distance) { distance = it }
        NumberField("Fuel efficiency (MPG)", efficiency) { efficiency = it }
        NumberField("Fuel price (per gallon)", price) { price = it }
        result?.let { (cost, fuel) ->
            ResultCard("Estimated fuel cost", cost)
            ResultCard("Fuel needed", fuel)
        }
    }
}

@Composable
fun FuelEfficiencyScreen(onBack: () -> Unit) {
    var distance by remember { mutableStateOf("300") }
    var fuel by remember { mutableStateOf("10") }

    val result = remember(distance, fuel) {
        val d = distance.toDoubleOrNull() ?: return@remember null
        val f = fuel.toDoubleOrNull() ?: return@remember null
        if (f <= 0) return@remember null
        val mpg = d / f
        if (mpg <= 0) return@remember null
        val l100 = 235.215 / mpg
        Triple(
            "${Formatters.number(mpg)} MPG",
            "${Formatters.number(l100)} L/100km",
            "${Formatters.number(mpg * 0.425144)} km/L",
        )
    }

    CalculatorScaffold(title = "Fuel Efficiency", onBack = onBack) {
        NumberField("Distance traveled (miles)", distance) { distance = it }
        NumberField("Fuel used (gallons)", fuel) { fuel = it }
        result?.let { (mpg, l100, kmpl) ->
            ResultCard("Miles per gallon", mpg)
            ResultCard("Liters per 100 km", l100)
            ResultCard("Kilometers per liter", kmpl)
        }
    }
}

data class GpaCourse(var credits: String, var grade: String)

@Composable
fun GpaScreen(onBack: () -> Unit) {
    val courses = remember { mutableStateListOf(GpaCourse("3", "A"), GpaCourse("4", "B+")) }
    val grades = listOf("A+", "A", "A-", "B+", "B", "B-", "C+", "C", "C-", "D+", "D", "D-", "F")
    val points = mapOf(
        "A+" to 4.0, "A" to 4.0, "A-" to 3.7,
        "B+" to 3.3, "B" to 3.0, "B-" to 2.7,
        "C+" to 2.3, "C" to 2.0, "C-" to 1.7,
        "D+" to 1.3, "D" to 1.0, "D-" to 0.7,
        "F" to 0.0,
    )

    val gpa = remember(courses.toList()) {
        var totalPoints = 0.0
        var totalCredits = 0.0
        courses.forEach { course ->
            val cr = course.credits.toDoubleOrNull() ?: return@forEach
            val gp = points[course.grade] ?: return@forEach
            if (cr > 0) {
                totalPoints += cr * gp
                totalCredits += cr
            }
        }
        if (totalCredits == 0.0) null else Formatters.number(totalPoints / totalCredits)
    }

    CalculatorScaffold(title = "GPA", onBack = onBack) {
        courses.forEachIndexed { index, course ->
            Text("Course ${index + 1}", modifier = Modifier.padding(bottom = 4.dp))
            NumberField("Credits", course.credits) { course.credits = it }
            DropdownField("Grade", grades.map { it to it }, course.grade) { course.grade = it }
        }
        androidx.compose.material3.TextButton(onClick = { courses.add(GpaCourse("3", "A")) }) {
            Text("Add course")
        }
        gpa?.let { ResultCard("GPA (4.0 scale)", it) }
    }
}

@Composable
fun OvulationScreen(onBack: () -> Unit) {
    var lastPeriod by remember { mutableStateOf(LocalDate.now().toString()) }
    var cycle by remember { mutableStateOf("28") }
    val fmt = remember { DateTimeFormatter.ofPattern("EEEE, MMM d, yyyy", Locale.US) }

    val results = remember(lastPeriod, cycle) {
        runCatching {
            val start = LocalDate.parse(lastPeriod)
            val len = cycle.toInt()
            require(len in 21..45)
            val ovulation = start.plusDays((len - 14).toLong())
            val fertileStart = ovulation.minusDays(5)
            val fertileEnd = ovulation.plusDays(1)
            val next = start.plusDays(len.toLong())
            Triple(
                ovulation.format(fmt),
                "${fertileStart.format(DateTimeFormatter.ofPattern("MMM d"))} – ${fertileEnd.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))}",
                next.format(fmt),
            )
        }.getOrNull()
    }

    CalculatorScaffold(title = "Ovulation", onBack = onBack) {
        Text(
            "Estimates ovulation from the first day of your last period and average cycle length.",
            modifier = Modifier.padding(bottom = 8.dp),
        )
        DateField("First day of last period", lastPeriod) { lastPeriod = it }
        NumberField("Average cycle length (days)", cycle) { cycle = it }
        results?.let { (ov, fertile, next) ->
            ResultCard("Estimated ovulation", ov)
            ResultCard("Fertile window", fertile)
            ResultCard("Next period (estimated)", next)
        }
    }
}

@Composable
fun HexScreen(onBack: () -> Unit) {
    var decimal by remember { mutableStateOf("255") }
    var hex by remember { mutableStateOf("FF") }

    CalculatorScaffold(title = "Hexadecimal", onBack = onBack) {
        NumberField("Decimal", decimal) {
            decimal = it
            it.toLongOrNull()?.let { n -> if (n >= 0) hex = n.toString(16).uppercase() }
        }
        decimal.toLongOrNull()?.let { ResultCard("Hexadecimal", "0x${it.toString(16).uppercase()}") }

        androidx.compose.material3.OutlinedTextField(
            value = hex,
            onValueChange = { v ->
                val cleaned = v.uppercase().removePrefix("0X")
                hex = cleaned
                cleaned.toLongOrNull(16)?.let { decimal = it.toString() }
            },
            label = { Text("Hexadecimal") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            colors = fieldColors(),
            singleLine = true,
        )
        hex.toLongOrNull(16)?.let { ResultCard("Decimal", Formatters.integer(it)) }
    }
}

@Composable
private fun DateField(label: String, value: String, onChange: (String) -> Unit) {
    androidx.compose.material3.OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = fieldColors(),
        singleLine = true,
        placeholder = { Text("YYYY-MM-DD") },
    )
}
