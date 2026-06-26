package com.techtree.clevcaleb.ui.calculators

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.techtree.clevcaleb.logic.MathEngine
import com.techtree.clevcaleb.theme.HermesColors

data class CalcButtonHelp(
    val title: String,
    val description: String,
    val whenToUse: String,
    val examples: List<String>,
)

/** Hold duration before showing button help. */
const val CALC_BUTTON_HELP_HOLD_MS = 800L

fun calcButtonHelp(key: String, angleMode: MathEngine.AngleMode = MathEngine.AngleMode.DEG): CalcButtonHelp {
  return helpByKey[key]
      ?: digitHelp(key)
      ?: calcButtonHelp("0").copy(title = key)
}

private fun digitHelp(key: String): CalcButtonHelp? {
  if (key.length != 1 || key[0] !in '0'..'9') return null
  return CalcButtonHelp(
      title = key,
      description = "Adds the digit $key to your expression.",
      whenToUse = "Use number keys to enter values, prices, measurements, or any part of a calculation.",
      examples = listOf(
          "Tap $key to type $key",
          "Build 25 by tapping 2 then 5",
          "Enter 3.14 with 3, ., 1, 4",
      ),
  )
}

private val helpByKey: Map<String, CalcButtonHelp> = mapOf(
    "0" to CalcButtonHelp(
        title = "0",
        description = "Adds a zero to your expression.",
        whenToUse = "Use for whole numbers, place value, or decimals like 10, 100, or 3.05.",
        examples = listOf(
            "100 for one hundred",
            "10.5 for ten and a half",
            "2000 for two thousand",
        ),
    ),
    "00" to CalcButtonHelp(
        title = "00",
        description = "Adds two zeros at once.",
        whenToUse = "Handy for round numbers ending in zeros so you do not have to tap 0 twice.",
        examples = listOf(
            "500 from 5 then 00",
            "1200 from 12 then 00",
            "80 from 8 then 00",
        ),
    ),
    "." to CalcButtonHelp(
        title = "Decimal point (.)",
        description = "Starts or continues the fractional part of a number.",
        whenToUse = "Use when a value is not a whole number — money, measurements, averages, and rates.",
        examples = listOf(
            "3.14 for pi (approximately)",
            "19.99 for a price",
            "0.5 for one half",
        ),
    ),
    "C" to CalcButtonHelp(
        title = "Clear (C)",
        description = "Clears the entire expression and starts fresh.",
        whenToUse = "Use when you made a mistake early on, want to start a new problem, or need a clean display.",
        examples = listOf(
            "Clear before a brand-new calculation",
            "Reset after finishing one problem",
            "Start over if the expression got confusing",
        ),
    ),
    "()" to CalcButtonHelp(
        title = "Parentheses ( )",
        description = "Adds ( or ) — opens a group, or closes one if there is an unmatched opening parenthesis.",
        whenToUse = "Use when order of operations matters — do one part first, then combine it with the rest.",
        examples = listOf(
            "(2+3)×4 = 20 — add first, then multiply",
            "100×(1+0.08) for price with 8% tax",
            "(50+30)÷2 for the average of two numbers",
        ),
    ),
    "%" to CalcButtonHelp(
        title = "Percent (%)",
        description = "Works with the number before it as a percentage.",
        whenToUse = "Use for tips, tax, discounts, markups, and any “out of 100” amount.",
        examples = listOf(
            "200+15% adds 15% of 200 (tip or tax)",
            "80−20% subtracts 20% of 80 (sale discount)",
            "50×10% finds 10% of 50",
        ),
    ),
    "÷" to CalcButtonHelp(
        title = "Divide (÷)",
        description = "Divides the value on the left by the value on the right.",
        whenToUse = "Use when splitting something into equal parts or finding how many times one number fits in another.",
        examples = listOf(
            "12÷3 = 4",
            "100÷4 splits 100 into four equal parts",
            "7.5÷2 for half of 7.5",
        ),
    ),
    "×" to CalcButtonHelp(
        title = "Multiply (×)",
        description = "Multiplies two values together.",
        whenToUse = "Use for area, totals, scaling recipes, repeated addition, or finding a fraction of a quantity.",
        examples = listOf(
            "6×7 = 42",
            "12×5 for five dozen items",
            "3.5×2 doubles 3.5",
        ),
    ),
    "−" to CalcButtonHelp(
        title = "Subtract (−)",
        description = "Subtracts the right value from the left value.",
        whenToUse = "Use for change due, differences, removing amounts, or finding how much is left.",
        examples = listOf(
            "50−17.25 for change from \$50",
            "100−35 for what remains",
            "8−3 = 5",
        ),
    ),
    "+" to CalcButtonHelp(
        title = "Add (+)",
        description = "Adds the value on the right to the value on the left.",
        whenToUse = "Use to combine amounts — bills, distances, times, or any running total.",
        examples = listOf(
            "15+27 = 42",
            "4.50+3.25 for a combined cost",
            "100+250+75 for a sum of several items",
        ),
    ),
    "=" to CalcButtonHelp(
        title = "Equals (=)",
        description = "Calculates the result of your expression and saves it to history.",
        whenToUse = "Press when the expression is complete and you want the final answer.",
        examples = listOf(
            "12+8 then = gives 20",
            "(100−15%) then = for a discounted price",
            "sin(30) then = for a trig result (in DEG mode)",
        ),
    ),
    "^" to CalcButtonHelp(
        title = "Power (^)",
        description = "Raises the left number to the power on the right (exponent).",
        whenToUse = "Use for squaring, cubing, compound growth, or any repeated multiplication.",
        examples = listOf(
            "2^8 = 256",
            "10^3 = 1000",
            "1.05^10 for roughly 5% growth over 10 periods",
        ),
    ),
    "⌫" to CalcButtonHelp(
        title = "Backspace (⌫)",
        description = "Deletes the last character you entered.",
        whenToUse = "Use for small typos without clearing the whole expression. Hold to delete quickly.",
        examples = listOf(
            "Typed 123 by mistake? Tap ⌫ once for 12",
            "Remove a stray operator before pressing =",
            "Hold ⌫ to erase several digits",
        ),
    ),
    "sci" to CalcButtonHelp(
        title = "Scientific keys (…)",
        description = "Opens or closes the extra row of advanced math buttons.",
        whenToUse = "Use when you need trig, logs, square roots, powers, or constants like π and e.",
        examples = listOf(
            "Open for sin, cos, tan on geometry problems",
            "Open for √ and x² on area or Pythagorean problems",
            "Close when you only need basic arithmetic",
        ),
    ),
    "sin" to CalcButtonHelp(
        title = "Sine (sin)",
        description = "Sine of an angle. In DEG mode, the angle is in degrees; in RAD mode, radians.",
        whenToUse = "Use in triangles, waves, and any problem involving an angle’s vertical ratio.",
        examples = listOf(
            "sin(30) = 0.5 in DEG mode",
            "sin(90) = 1 in DEG mode",
            "Height = distance × sin(angle)",
        ),
    ),
    "cos" to CalcButtonHelp(
        title = "Cosine (cos)",
        description = "Cosine of an angle. In DEG mode, the angle is in degrees; in RAD mode, radians.",
        whenToUse = "Use for horizontal components, adjacent sides in triangles, and periodic patterns.",
        examples = listOf(
            "cos(60) = 0.5 in DEG mode",
            "cos(0) = 1",
            "Run = hypotenuse × cos(angle)",
        ),
    ),
    "tan" to CalcButtonHelp(
        title = "Tangent (tan)",
        description = "Tangent of an angle — sine divided by cosine.",
        whenToUse = "Use for slope, rise over run, or when you know an angle and need an opposite/adjacent ratio.",
        examples = listOf(
            "tan(45) = 1 in DEG mode",
            "Height = distance × tan(angle)",
            "Slope as tan(angle from horizontal)",
        ),
    ),
    "log" to CalcButtonHelp(
        title = "Logarithm base 10 (log)",
        description = "Finds the power you raise 10 to in order to get a number.",
        whenToUse = "Use with orders of magnitude — decibels, pH-style scales, or “how many digits” style problems.",
        examples = listOf(
            "log(100) = 2 because 10² = 100",
            "log(1000) = 3",
            "log(50) ≈ 1.7",
        ),
    ),
    "ln" to CalcButtonHelp(
        title = "Natural log (ln)",
        description = "Logarithm base e (≈ 2.718). Inverse of the eˣ growth function.",
        whenToUse = "Use in science and finance for continuous growth or decay over time.",
        examples = listOf(
            "ln(e) = 1",
            "ln(1) = 0",
            "Growth time problems in science class",
        ),
    ),
    "√" to CalcButtonHelp(
        title = "Square root (√)",
        description = "Finds the number that, multiplied by itself, gives the value inside.",
        whenToUse = "Use for side lengths from areas, Pythagorean theorem, or undoing x².",
        examples = listOf(
            "√(16) = 4",
            "√(2) ≈ 1.414",
            "Side of a square with area 25: √(25) = 5",
        ),
    ),
    "x²" to CalcButtonHelp(
        title = "Square (x²)",
        description = "Multiplies a number by itself (raises it to the 2nd power).",
        whenToUse = "Use for areas of squares, formulas with squared terms, or distance calculations.",
        examples = listOf(
            "5 then x² gives 25",
            "3² = 9",
            "Part of a² + b² = c² (Pythagorean theorem)",
        ),
    ),
    "π" to CalcButtonHelp(
        title = "Pi (π)",
        description = "Inserts π (≈ 3.14159) — the ratio of a circle’s circumference to its diameter.",
        whenToUse = "Use for anything circular: circumferences, areas, and arcs.",
        examples = listOf(
            "2×π×r for circumference",
            "π×r² for circle area",
            "π ≈ 3.14 for quick estimates",
        ),
    ),
    "e" to CalcButtonHelp(
        title = "Euler’s number (e)",
        description = "Inserts e (≈ 2.71828), the base of natural logarithms and continuous growth.",
        whenToUse = "Use in compound/continuous growth, science formulas, and with ln.",
        examples = listOf(
            "e^1 ≈ 2.718",
            "ln(e) = 1",
            "Population or interest models in advanced math",
        ),
    ),
    "DEG" to CalcButtonHelp(
        title = "Angle mode: DEG",
        description = "Trigonometry uses degrees (0–360 for a full circle). Tap to switch to radians (RAD).",
        whenToUse = "Keep DEG for everyday angles — most homework, construction, and maps use degrees.",
        examples = listOf(
            "sin(90) = 1 in DEG mode",
            "A right angle is 90 degrees",
            "Switch to RAD only if a problem says radians",
        ),
    ),
    "RAD" to CalcButtonHelp(
        title = "Angle mode: RAD",
        description = "Trigonometry uses radians (2π ≈ 6.28 for a full circle). Tap to switch back to degrees (DEG).",
        whenToUse = "Use RAD when a formula, textbook, or engineering problem specifies radians.",
        examples = listOf(
            "sin(π/2) = 1 in RAD mode",
            "π radians = 180 degrees",
            "Calculus and physics often use radians",
        ),
    ),
)

@Composable
fun CalcButtonHelpDialog(
    help: CalcButtonHelp,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(help.title) },
        text = {
            Column {
                Text(help.description)
                Spacer(Modifier.height(12.dp))
                Text("When to use it", fontWeight = FontWeight.SemiBold)
                Text(help.whenToUse)
                Spacer(Modifier.height(12.dp))
                Text("Examples", fontWeight = FontWeight.SemiBold)
                help.examples.forEach { example ->
                    Text("• $example")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Got it") }
        },
        containerColor = HermesColors.Card,
        titleContentColor = HermesColors.Foreground,
        textContentColor = HermesColors.Foreground,
    )
}
