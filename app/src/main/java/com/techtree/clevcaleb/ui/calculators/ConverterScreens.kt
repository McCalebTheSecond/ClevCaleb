package com.techtree.clevcaleb.ui.calculators

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.techtree.clevcaleb.logic.CurrencyRepository
import com.techtree.clevcaleb.logic.Formatters
import com.techtree.clevcaleb.logic.UnitCategory
import com.techtree.clevcaleb.logic.UnitConverterLogic
import com.techtree.clevcaleb.logic.WorldTimeData
import com.techtree.clevcaleb.theme.HermesColors
import com.techtree.clevcaleb.ui.components.CalculatorScaffold
import com.techtree.clevcaleb.ui.components.ResultCard
import kotlinx.coroutines.delay
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitConverterScreen(onBack: () -> Unit) {
    var category by remember { mutableStateOf(UnitCategory.LENGTH) }
    val units = UnitConverterLogic.categories[category] ?: emptyList()
    var fromId by remember(category) {
        val (from, _) = UnitConverterLogic.defaultUsUnits(category)
        mutableStateOf(from)
    }
    var toId by remember(category) {
        val (_, to) = UnitConverterLogic.defaultUsUnits(category)
        mutableStateOf(to)
    }
    var value by remember { mutableStateOf("1") }

    val result = remember(value, fromId, toId, category) {
        value.toDoubleOrNull()?.let { v ->
            UnitConverterLogic.convert(v, fromId, toId, category)?.let { Formatters.number(it, 6) }
        } ?: "—"
    }

    CalculatorScaffold(title = "Unit Converter", onBack = onBack) {
        DropdownField("Category", UnitCategory.entries.map { it to it.label }, category) {
            category = it
            val (from, to) = UnitConverterLogic.defaultUsUnits(it)
            fromId = from
            toId = to
        }
        NumberField("Value", value) { value = it }
        DropdownField("From", units.map { it.id to it.label }, fromId) { fromId = it }
        DropdownField("To", units.map { it.id to it.label }, toId) { toId = it }
        ResultCard("Result", result)
    }
}

@Composable
fun CurrencyConverterScreen(onBack: () -> Unit) {
    var amount by remember { mutableStateOf("100") }
    var from by remember { mutableStateOf("USD") }
    var to by remember { mutableStateOf("CAD") }
    var rates by remember { mutableStateOf<Map<String, Double>?>(null) }

    LaunchedEffect(Unit) {
        rates = CurrencyRepository.fetchRates()
    }

    val result = remember(amount, from, to, rates) {
        val n = amount.toDoubleOrNull() ?: return@remember "—"
        val r = rates ?: return@remember "Loading…"
        val converted = CurrencyRepository.convert(n, from, to, r)
        val symbol = CurrencyRepository.currencies.find { it.code == to }?.symbol ?: ""
        "$symbol${Formatters.number(converted)}"
    }

    CalculatorScaffold(title = "Currency Converter", onBack = onBack) {
        if (rates == null) CircularProgressIndicator(color = HermesColors.Primary)
        NumberField("Amount", amount) { amount = it }
        DropdownField("From", CurrencyRepository.currencies.map { it.code to "${it.code} — ${it.name}" }, from) { from = it }
        DropdownField("To", CurrencyRepository.currencies.map { it.code to "${it.code} — ${it.name}" }, to) { to = it }
        ResultCard("Converted amount", result)
    }
}

@Composable
fun WorldTimeScreen(onBack: () -> Unit) {
    var fromTz by remember { mutableStateOf("America/Chicago") }
    var toTz by remember { mutableStateOf("America/New_York") }
    var now by remember { mutableStateOf(ZonedDateTime.now()) }

    LaunchedEffect(Unit) {
        while (true) {
            now = ZonedDateTime.now()
            delay(1000)
        }
    }

    val formatter = remember {
        DateTimeFormatter.ofPattern("EEE, MMM d, h:mm a z", Locale.US)
    }

    CalculatorScaffold(title = "US Time", onBack = onBack) {
        DropdownField(
            "From city",
            WorldTimeData.cities.map { it.timezone to it.label },
            fromTz,
        ) { fromTz = it }
        ResultCard("Time", now.withZoneSameInstant(ZoneId.of(fromTz)).format(formatter))

        DropdownField(
            "To city",
            WorldTimeData.cities.map { it.timezone to it.label },
            toTz,
        ) { toTz = it }
        ResultCard("Time", now.withZoneSameInstant(ZoneId.of(toTz)).format(formatter))
    }
}

@Composable
fun Panel(content: @Composable () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = HermesColors.Card),
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
    ) {
        androidx.compose.foundation.layout.Column(modifier = Modifier.padding(12.dp)) {
            content()
        }
    }
}
