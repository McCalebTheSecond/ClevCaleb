package com.techtree.clevcaleb.ui.calculators

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.techtree.clevcaleb.logic.FinanceCalculations
import com.techtree.clevcaleb.logic.Formatters
import com.techtree.clevcaleb.ui.components.CalculatorScaffold
import com.techtree.clevcaleb.ui.components.ResultCard

@Composable
fun PercentageScreen(onBack: () -> Unit) {
    var mode by remember { mutableStateOf("increase") }
    var a by remember { mutableStateOf("100") }
    var b by remember { mutableStateOf("15") }

    val (primary, secondary) = remember(mode, a, b) {
        val x = a.toDoubleOrNull() ?: return@remember "—" to ""
        val y = b.toDoubleOrNull() ?: return@remember "—" to ""
        when (mode) {
            "increase" -> Formatters.number(x * (1 + y / 100)) to "+${Formatters.number(x * y / 100)} increase"
            "decrease" -> Formatters.number(x * (1 - y / 100)) to "−${Formatters.number(x * y / 100)} decrease"
            "of" -> Formatters.number(x * y / 100) to "$y% of $x"
            else -> if (x == 0.0) "—" to "Cannot divide by zero"
            else Formatters.number(y / x * 100) + "%" to "$y is what % of $x"
        }
    }

    CalculatorScaffold(title = "Percentage", onBack = onBack) {
        DropdownField(
            "Calculation type",
            listOf(
                "increase" to "Percentage increase",
                "decrease" to "Percentage decrease",
                "of" to "X% of a number",
                "what" to "What % is X of Y?",
            ),
            mode,
        ) { mode = it }
        NumberField(if (mode == "what") "Total (Y)" else "Value", a) { a = it }
        NumberField(if (mode == "what") "Part (X)" else "Percent", b) { b = it }
        ResultCard("Result", primary)
        if (secondary.isNotEmpty()) ResultCard("Details", secondary)
    }
}

@Composable
fun DiscountScreen(onBack: () -> Unit) {
    var price by remember { mutableStateOf("99.99") }
    var discount by remember { mutableStateOf("20") }

    val result = remember(price, discount) {
        val p = price.toDoubleOrNull() ?: return@remember null
        val d = discount.toDoubleOrNull() ?: return@remember null
        val saved = p * d / 100
        Formatters.currency(p - saved) to Formatters.currency(saved)
    }

    CalculatorScaffold(title = "Discount", onBack = onBack) {
        NumberField("Original price", price) { price = it }
        NumberField("Discount (%)", discount) { discount = it }
        result?.let { (discounted, saved) ->
            ResultCard("Discounted price", discounted)
            ResultCard("You save", saved)
        }
    }
}

@Composable
fun LoanScreen(onBack: () -> Unit) {
    var principal by remember { mutableStateOf("10000") }
    var rate by remember { mutableStateOf("5.5") }
    var years by remember { mutableStateOf("5") }

    val result = remember(principal, rate, years) {
        val p = principal.toDoubleOrNull() ?: return@remember null
        val r = rate.toDoubleOrNull() ?: return@remember null
        val y = years.toDoubleOrNull() ?: return@remember null
        FinanceCalculations.loanPayment(p, r, y)
    }

    CalculatorScaffold(title = "Loan", onBack = onBack) {
        NumberField("Loan principal", principal) { principal = it }
        NumberField("Annual interest rate (%)", rate) { rate = it }
        NumberField("Term (years)", years) { years = it }
        result?.let { (monthly, total, interest) ->
            ResultCard("Monthly payment", Formatters.currency(monthly))
            ResultCard("Total payment", Formatters.currency(total))
            ResultCard("Total interest", Formatters.currency(interest))
        }
    }
}

@Composable
fun TipScreen(onBack: () -> Unit) {
    var bill by remember { mutableStateOf("85") }
    var tax by remember { mutableStateOf("6.80") }
    var tipPct by remember { mutableStateOf("18") }
    var people by remember { mutableStateOf("2") }
    var excludeTax by remember { mutableStateOf(true) }

    val result = remember(bill, tax, tipPct, people, excludeTax) {
        val b = bill.toDoubleOrNull() ?: return@remember null
        val t = tax.toDoubleOrNull() ?: 0.0
        val pct = tipPct.toDoubleOrNull() ?: return@remember null
        val n = people.toIntOrNull() ?: return@remember null
        if (n < 1) return@remember null
        FinanceCalculations.tip(b, t, pct, n, excludeTax)
    }

    CalculatorScaffold(title = "Tip", onBack = onBack) {
        NumberField("Bill amount", bill) { bill = it }
        NumberField("Tax amount", tax) { tax = it }
        NumberField("Tip (%)", tipPct) { tipPct = it }
        NumberField("Number of people", people) { people = it }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = excludeTax,
                onCheckedChange = { excludeTax = it },
                colors = CheckboxDefaults.colors(checkedColor = com.techtree.clevcaleb.theme.HermesColors.NousBlue),
            )
            Text("Calculate tip on pre-tax amount")
        }
        result?.let { (tip, total, per) ->
            ResultCard("Tip amount", Formatters.currency(tip))
            ResultCard("Total with tip", Formatters.currency(total))
            ResultCard("Per person", Formatters.currency(per))
        }
    }
}

@Composable
fun SalesTaxScreen(onBack: () -> Unit) {
    var price by remember { mutableStateOf("49.99") }
    var rate by remember { mutableStateOf("8.25") }

    val result = remember(price, rate) {
        val p = price.toDoubleOrNull() ?: return@remember null
        val r = rate.toDoubleOrNull() ?: return@remember null
        val tax = p * r / 100
        Formatters.currency(tax) to Formatters.currency(p + tax)
    }

    CalculatorScaffold(title = "Sales Tax", onBack = onBack) {
        NumberField("Price before tax", price) { price = it }
        NumberField("Tax rate (%)", rate) { rate = it }
        result?.let { (tax, total) ->
            ResultCard("Tax amount", tax)
            ResultCard("Total price", total)
        }
    }
}

@Composable
fun SavingsScreen(onBack: () -> Unit) {
    var initial by remember { mutableStateOf("1000") }
    var monthly by remember { mutableStateOf("200") }
    var rate by remember { mutableStateOf("4.5") }
    var years by remember { mutableStateOf("10") }
    var tax by remember { mutableStateOf("0") }

    val result = remember(initial, monthly, rate, years, tax) {
        val i = initial.toDoubleOrNull() ?: return@remember null
        val m = monthly.toDoubleOrNull() ?: return@remember null
        val r = rate.toDoubleOrNull() ?: return@remember null
        val y = years.toDoubleOrNull() ?: return@remember null
        val t = tax.toDoubleOrNull() ?: 0.0
        FinanceCalculations.savingsBalance(i, m, r, y, t)
    }

    CalculatorScaffold(title = "Savings", onBack = onBack) {
        NumberField("Initial deposit", initial) { initial = it }
        NumberField("Monthly contribution", monthly) { monthly = it }
        NumberField("Annual interest rate (%)", rate) { rate = it }
        NumberField("Years", years) { years = it }
        NumberField("Tax on interest (%)", tax) { tax = it }
        result?.let { (balance, contributed, interest) ->
            ResultCard("Final balance", Formatters.currency(balance))
            ResultCard("Total contributed", Formatters.currency(contributed))
            ResultCard("Interest earned (after tax)", Formatters.currency(interest))
        }
    }
}

@Composable
fun UnitPriceScreen(onBack: () -> Unit) {
    var priceA by remember { mutableStateOf("4.99") }
    var qtyA by remember { mutableStateOf("16") }
    var priceB by remember { mutableStateOf("6.49") }
    var qtyB by remember { mutableStateOf("24") }

    val unitA = remember(priceA, qtyA) {
        val p = priceA.toDoubleOrNull()
        val q = qtyA.toDoubleOrNull()
        if (p != null && q != null && q > 0) Formatters.currency(p / q) else null
    }
    val unitB = remember(priceB, qtyB) {
        val p = priceB.toDoubleOrNull()
        val q = qtyB.toDoubleOrNull()
        if (p != null && q != null && q > 0) Formatters.currency(p / q) else null
    }
    val best = remember(priceA, qtyA, priceB, qtyB) {
        val p1 = priceA.toDoubleOrNull()
        val q1 = qtyA.toDoubleOrNull()
        val p2 = priceB.toDoubleOrNull()
        val q2 = qtyB.toDoubleOrNull()
        if (p1 == null || q1 == null || q1 <= 0 || p2 == null || q2 == null || q2 <= 0) return@remember null
        if (p1 / q1 <= p2 / q2) "Product A" else "Product B"
    }

    CalculatorScaffold(title = "Unit Price", onBack = onBack) {
        Text("Product A")
        NumberField("Price", priceA) { priceA = it }
        NumberField("Quantity", qtyA) { qtyA = it }
        unitA?.let { ResultCard("Product A unit price", it) }

        Text("Product B", modifier = Modifier.padding(top = 8.dp))
        NumberField("Price", priceB) { priceB = it }
        NumberField("Quantity", qtyB) { qtyB = it }
        unitB?.let { ResultCard("Product B unit price", it) }
        best?.let { ResultCard("Best value", it) }
    }
}
