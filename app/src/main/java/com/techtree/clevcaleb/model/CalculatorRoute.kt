package com.techtree.clevcaleb.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.Hexagon
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Woman
import androidx.compose.ui.graphics.vector.ImageVector

/** Calculator types aligned with ClevCalc naming and feature set. */
enum class CalculatorRoute(
    val route: String,
    /** ClevCalc-style display name */
    val title: String,
    val icon: ImageVector,
) {
    Main("main", "Calculator", Icons.Filled.Calculate),
    UnitConverter("unit", "Unit Converter", Icons.Filled.Straighten),
    Currency("currency", "Currencies", Icons.Filled.AttachMoney),
    Percentage("percentage", "Percent", Icons.Filled.Percent),
    Discount("discount", "Discount", Icons.Filled.Percent),
    Loan("loan", "Loan", Icons.Filled.AttachMoney),
    Date("date", "Date", Icons.Filled.CalendarMonth),
    Health("health", "Body Metrics", Icons.Filled.Favorite),
    FuelCost("fuel_cost", "Fuel Cost", Icons.Filled.LocalGasStation),
    FuelEfficiency("fuel_efficiency", "Fuel Efficiency", Icons.Filled.Speed),
    Gpa("gpa", "Grade Average", Icons.Filled.Grade),
    Tip("tip", "Tip", Icons.Filled.Receipt),
    SalesTax("sales_tax", "Sales Tax", Icons.Filled.Receipt),
    UnitPrice("unit_price", "Unit Price", Icons.Filled.ShoppingCart),
    WorldTime("world_time", "US Time", Icons.Filled.Public),
    Ovulation("ovulation", "Ovulation", Icons.Filled.Woman),
    Hex("hex", "Hex", Icons.Filled.Hexagon),
    Savings("savings", "Savings", Icons.AutoMirrored.Filled.ShowChart),
    ;

    companion object {
        fun fromRoute(route: String): CalculatorRoute? = entries.find { it.route == route }
    }
}
