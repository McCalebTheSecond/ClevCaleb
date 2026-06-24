package com.techtree.clevcaleb.logic

import kotlin.math.pow

enum class UnitCategory(val label: String) {
    LENGTH("Length"),
    WEIGHT("Weight"),
    AREA("Area"),
    VOLUME("Volume"),
    TIME("Time"),
    TEMPERATURE("Temperature"),
    PRESSURE("Pressure"),
    SPEED("Speed"),
    FUEL("Fuel Efficiency"),
    DATA("Data Size"),
}

data class UnitDef(val id: String, val label: String, val toBase: (Double) -> Double, val fromBase: (Double) -> Double)

object UnitConverterLogic {
    private fun linear(factor: Double) = UnitDef("", "", { v -> v * factor }, { v -> v / factor })

    private fun units(vararg pairs: Pair<String, Pair<String, Double>>): List<UnitDef> =
        pairs.map { (id, labelFactor) ->
            val (label, factor) = labelFactor
            UnitDef(id, label, { v -> v * factor }, { v -> v / factor })
        }

    val categories: Map<UnitCategory, List<UnitDef>> = mapOf(
        UnitCategory.LENGTH to units(
            "mm" to ("Millimeter (mm)" to 0.001),
            "cm" to ("Centimeter (cm)" to 0.01),
            "m" to ("Meter (m)" to 1.0),
            "km" to ("Kilometer (km)" to 1000.0),
            "in" to ("Inch (in)" to 0.0254),
            "ft" to ("Foot (ft)" to 0.3048),
            "yd" to ("Yard (yd)" to 0.9144),
            "mi" to ("Mile (mi)" to 1609.344),
        ),
        UnitCategory.WEIGHT to units(
            "mg" to ("Milligram (mg)" to 0.000001),
            "g" to ("Gram (g)" to 0.001),
            "kg" to ("Kilogram (kg)" to 1.0),
            "oz" to ("Ounce (oz)" to 0.0283495),
            "lb" to ("Pound (lb)" to 0.453592),
            "t" to ("Metric ton (t)" to 1000.0),
        ),
        UnitCategory.AREA to units(
            "sqm" to ("Square meter (m²)" to 1.0),
            "sqkm" to ("Square kilometer (km²)" to 1_000_000.0),
            "sqft" to ("Square foot (ft²)" to 0.092903),
            "sqmi" to ("Square mile (mi²)" to 2_589_988.11),
            "acre" to ("Acre" to 4046.86),
            "ha" to ("Hectare (ha)" to 10_000.0),
        ),
        UnitCategory.VOLUME to units(
            "ml" to ("Milliliter (mL)" to 0.001),
            "l" to ("Liter (L)" to 1.0),
            "gal" to ("US Gallon (gal)" to 3.78541),
            "qt" to ("US Quart (qt)" to 0.946353),
            "cup" to ("US Cup" to 0.236588),
            "floz" to ("US Fluid ounce (fl oz)" to 0.0295735),
            "cuft" to ("Cubic foot (ft³)" to 28.3168),
        ),
        UnitCategory.TIME to units(
            "ms" to ("Millisecond" to 0.001),
            "s" to ("Second" to 1.0),
            "min" to ("Minute" to 60.0),
            "hr" to ("Hour" to 3600.0),
            "day" to ("Day" to 86400.0),
            "wk" to ("Week" to 604800.0),
            "yr" to ("Year (365d)" to 31_536_000.0),
        ),
        UnitCategory.TEMPERATURE to listOf(
            UnitDef("c", "Celsius (°C)", { it }, { it }),
            UnitDef("f", "Fahrenheit (°F)", { (it - 32) * 5 / 9 }, { it * 9 / 5 + 32 }),
            UnitDef("k", "Kelvin (K)", { it - 273.15 }, { it + 273.15 }),
        ),
        UnitCategory.PRESSURE to units(
            "pa" to ("Pascal (Pa)" to 1.0),
            "kpa" to ("Kilopascal (kPa)" to 1000.0),
            "bar" to ("Bar" to 100_000.0),
            "atm" to ("Atmosphere (atm)" to 101_325.0),
            "psi" to ("PSI" to 6894.76),
            "mmhg" to ("mmHg" to 133.322),
        ),
        UnitCategory.SPEED to units(
            "mps" to ("Meters/sec (m/s)" to 1.0),
            "kph" to ("Kilometers/hr (km/h)" to 1 / 3.6),
            "mph" to ("Miles/hr (mph)" to 0.44704),
            "knot" to ("Knot" to 0.514444),
        ),
        UnitCategory.FUEL to listOf(
            UnitDef("mpg", "Miles per gallon (MPG)", { if (it > 0) 235.215 / it else 0.0 }, { if (it > 0) 235.215 / it else 0.0 }),
            UnitDef("l100", "Liters per 100 km", { it }, { it }),
            UnitDef("kmpl", "Kilometers per liter", { if (it > 0) 100 / it else 0.0 }, { if (it > 0) 100 / it else 0.0 }),
        ),
        UnitCategory.DATA to units(
            "b" to ("Byte (B)" to 1.0),
            "kb" to ("Kilobyte (KB)" to 1024.0),
            "mb" to ("Megabyte (MB)" to 1024.0.pow(2)),
            "gb" to ("Gigabyte (GB)" to 1024.0.pow(3)),
            "tb" to ("Terabyte (TB)" to 1024.0.pow(4)),
        ),
    )

    fun convert(value: Double, fromId: String, toId: String, category: UnitCategory): Double? {
        val unitList = categories[category] ?: return null
        val from = unitList.find { it.id == fromId } ?: return null
        val to = unitList.find { it.id == toId } ?: return null
        return to.fromBase(from.toBase(value))
    }
}
