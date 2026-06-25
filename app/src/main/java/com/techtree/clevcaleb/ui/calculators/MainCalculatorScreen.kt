package com.techtree.clevcaleb.ui.calculators

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.techtree.clevcaleb.logic.Formatters
import com.techtree.clevcaleb.logic.MathEngine
import com.techtree.clevcaleb.theme.HermesColors
import com.techtree.clevcaleb.ui.AppViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeoutOrNull

/** Keypad sizing tuned to match ClevCalc's large, thumb-friendly layout. */
private object CalcSizing {
    val keyHeight = 76.dp
    val sciKeyHeight = 54.dp
    val utilityRowHeight = 52.dp
    val keyPadding = 4.dp
    val keyCorner = 8.dp

    val displayText = TextStyle(fontSize = 44.sp, fontWeight = FontWeight.Normal, lineHeight = 48.sp)
    val previewText = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.Normal)
    val numberKeyText = TextStyle(fontSize = 30.sp, fontWeight = FontWeight.Medium)
    val operatorKeyText = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.Medium)
    val equalsKeyText = TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Bold)
    val utilityText = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.Medium)
    val scientificText = TextStyle(fontSize = 17.sp, fontWeight = FontWeight.Medium)
    val utilityIconSize = 32.dp
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainCalculatorScreen(
    viewModel: AppViewModel,
    onOpenDrawer: () -> Unit,
    onSettings: () -> Unit,
) {
    val keepRecord by viewModel.keepCalcRecord.collectAsState()
    val vibration by viewModel.buttonVibration.collectAsState()
    val savedExpr by viewModel.lastExpression.collectAsState()
    val history by viewModel.history.collectAsState()

    var expression by remember { mutableStateOf("") }
    var scientificOpen by remember { mutableStateOf(false) }
    var angleMode by remember { mutableStateOf(MathEngine.AngleMode.DEG) }
    var showHistory by remember { mutableStateOf(false) }
    var decimalPlaces by remember { mutableStateOf(10) }
    var showDecimalDialog by remember { mutableStateOf(false) }
    var preview by remember { mutableStateOf("") }
    var helpKey by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(expression, angleMode, decimalPlaces) {
        if (expression.isEmpty()) {
            preview = ""
            return@LaunchedEffect
        }
        delay(120)
        preview = MathEngine.evaluate(expression, angleMode)?.let { Formatters.calculator(it, decimalPlaces) } ?: ""
    }

    LaunchedEffect(savedExpr, keepRecord) {
        if (keepRecord && savedExpr.isNotEmpty() && expression.isEmpty()) {
            expression = savedExpr
        }
    }

    DisposableEffect(keepRecord) {
        onDispose {
            if (keepRecord) viewModel.flushLastExpression(expression)
        }
    }

    val view = LocalView.current
    fun haptic() {
        if (vibration) view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
    }

    fun showHelp(key: String) {
        haptic()
        helpKey = key
    }

    fun append(token: String) {
        expression += token
        if (keepRecord) viewModel.setLastExpression(expression)
    }

    fun backspace() {
        if (expression.isNotEmpty()) {
            expression = expression.dropLast(1)
            if (keepRecord) viewModel.setLastExpression(expression)
        }
    }

    fun handleKey(key: String) {
        haptic()
        when (key) {
            "C" -> {
                expression = ""
                if (keepRecord) viewModel.setLastExpression("", immediate = true)
            }
            "⌫" -> backspace()
            "=" -> {
                val result = MathEngine.evaluate(expression, angleMode)
                if (result != null) {
                    val formatted = Formatters.calculator(result, decimalPlaces)
                    val entry = "$expression = $formatted"
                    viewModel.addHistory(entry)
                    expression = formatted
                    if (keepRecord) viewModel.setLastExpression(expression, immediate = true)
                }
            }
            "()" -> append("(")
            "÷" -> append("÷")
            "×" -> append("×")
            "−" -> append("−")
            "+" -> append("+")
            "%" -> append("%")
            "^" -> append("^")
            "00" -> append("00")
            "√" -> append("sqrt(")
            "x²" -> append("^2")
            "π" -> append("π")
            "e" -> append("e")
            "sin", "cos", "tan", "log", "ln" -> append("$key(")
            else -> append(key)
        }
    }

    com.techtree.clevcaleb.ui.ClevCalcScaffold(
        onOpenDrawer = onOpenDrawer,
        onHistory = { showHistory = true },
        onSettings = onSettings,
        onDecimalPlaces = { showDecimalDialog = true },
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(12.dp)
                    .background(HermesColors.Card, RoundedCornerShape(8.dp))
                    .border(1.dp, HermesColors.Border, RoundedCornerShape(8.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.BottomEnd,
            ) {
                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
                    Text(
                        text = preview.ifEmpty { " " },
                        style = CalcSizing.previewText,
                        color = HermesColors.MutedForeground,
                    )
                    BasicTextField(
                        value = expression,
                        onValueChange = {
                            expression = it
                            if (keepRecord) viewModel.setLastExpression(it)
                        },
                        textStyle = CalcSizing.displayText.copy(
                            color = HermesColors.Foreground,
                            textAlign = TextAlign.End,
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
            ) {
                CalcUtilityKey(
                    label = "…",
                    modifier = Modifier.weight(1f),
                    onHaptic = ::haptic,
                    onShowHelp = { showHelp("sci") },
                ) {
                    scientificOpen = !scientificOpen
                }
                CalcUtilityKey(
                    label = "^",
                    modifier = Modifier.weight(1f),
                    onShowHelp = { showHelp("^") },
                ) { handleKey("^") }
                CalcUtilityKey(
                    label = "⌫",
                    modifier = Modifier.weight(1f),
                    repeatOnHold = true,
                    onHaptic = ::haptic,
                    onShowHelp = { showHelp("⌫") },
                    onClick = ::backspace,
                )
            }

            if (scientificOpen) {
                val sciKeys = listOf("sin", "cos", "tan", "log", "ln", "√", "x²", "π", "e", "DEG")
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
                    sciKeys.chunked(5).forEach { row ->
                        Column(modifier = Modifier.weight(1f)) {
                            row.forEach { key ->
                                CalcKey(
                                    label = key,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(CalcSizing.keyPadding)
                                        .height(CalcSizing.sciKeyHeight),
                                    isOperator = true,
                                    compact = true,
                                    onClick = {
                                        if (key == "DEG") {
                                            haptic()
                                            angleMode = if (angleMode == MathEngine.AngleMode.DEG) {
                                                MathEngine.AngleMode.RAD
                                            } else {
                                                MathEngine.AngleMode.DEG
                                            }
                                        } else {
                                            handleKey(key)
                                        }
                                    },
                                    onShowHelp = {
                                        if (key == "DEG") {
                                            showHelp(
                                                if (angleMode == MathEngine.AngleMode.DEG) "DEG" else "RAD",
                                            )
                                        } else {
                                            showHelp(key)
                                        }
                                    },
                                )
                            }
                        }
                    }
                }
            }

            ClevCalcKeypad(onKey = ::handleKey, onShowHelp = ::showHelp)
        }
    }

    helpKey?.let { key ->
        CalcButtonHelpDialog(
            help = calcButtonHelp(key, angleMode),
            onDismiss = { helpKey = null },
        )
    }

    if (showHistory) {
        AlertDialog(
            onDismissRequest = { showHistory = false },
            title = { Text("History") },
            text = {
                Column {
                    if (history.isEmpty()) {
                        Text("No calculations yet.")
                    } else {
                        history.forEach { item ->
                            TextButton(onClick = {
                                expression = item.substringBefore(" = ")
                                showHistory = false
                            }) {
                                Text(item, color = HermesColors.Foreground)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.setHistory(emptyList())
                    showHistory = false
                }) { Text("Clear") }
            },
            dismissButton = {
                TextButton(onClick = { showHistory = false }) { Text("Close") }
            },
            containerColor = HermesColors.Card,
            titleContentColor = HermesColors.Foreground,
            textContentColor = HermesColors.Foreground,
        )
    }

    if (showDecimalDialog) {
        AlertDialog(
            onDismissRequest = { showDecimalDialog = false },
            title = { Text("Decimal places") },
            text = {
                Column {
                    listOf(2, 4, 6, 8, 10).forEach { n ->
                        TextButton(onClick = {
                            decimalPlaces = n
                            showDecimalDialog = false
                        }) { Text("$n decimal places") }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showDecimalDialog = false }) { Text("Cancel") }
            },
            containerColor = HermesColors.Card,
            titleContentColor = HermesColors.Foreground,
        )
    }
}

@Composable
private fun ClevCalcKeypad(
    onKey: (String) -> Unit,
    onShowHelp: (String) -> Unit,
) {
    val rows = listOf(
        listOf("C", "()", "%", "÷"),
        listOf("7", "8", "9", "×"),
        listOf("4", "5", "6", "−"),
        listOf("1", "2", "3", "+"),
        listOf("0", "00", ".", "="),
    )
    Column(modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)) {
        rows.forEach { row ->
            Row(modifier = Modifier.fillMaxWidth()) {
                row.forEach { key ->
                    val isEquals = key == "="
                    val isOperator = key in listOf("÷", "×", "−", "+", "=") || key == "C" || key == "()" || key == "%"
                    CalcKey(
                        label = key,
                        modifier = Modifier
                            .weight(1f)
                            .padding(CalcSizing.keyPadding)
                            .height(CalcSizing.keyHeight),
                        isOperator = isOperator,
                        isEquals = isEquals,
                        onClick = { onKey(key) },
                        onShowHelp = { onShowHelp(key) },
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CalcKey(
    label: String,
    modifier: Modifier = Modifier,
    isOperator: Boolean = false,
    isEquals: Boolean = false,
    compact: Boolean = false,
    onClick: () -> Unit,
    onShowHelp: () -> Unit = {},
) {
    val bg = when {
        isEquals -> HermesColors.NousBlue
        isOperator -> HermesColors.Secondary
        else -> HermesColors.Card
    }
    val textStyle = when {
        isEquals -> CalcSizing.equalsKeyText
        compact -> CalcSizing.scientificText
        isOperator -> CalcSizing.operatorKeyText
        else -> CalcSizing.numberKeyText
    }
    val shape = RoundedCornerShape(CalcSizing.keyCorner)
    val interactionSource = remember { MutableInteractionSource() }
    Surface(
        modifier = modifier.combinedClickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = onClick,
            onLongClick = onShowHelp,
        ),
        shape = shape,
        color = bg,
        border = BorderStroke(1.dp, HermesColors.Border),
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = label,
                style = textStyle,
                color = HermesColors.Foreground,
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CalcUtilityKey(
    label: String,
    modifier: Modifier = Modifier,
    repeatOnHold: Boolean = false,
    onHaptic: () -> Unit = {},
    onShowHelp: () -> Unit = {},
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val keyModifier = modifier
        .padding(horizontal = 4.dp)
        .height(CalcSizing.utilityRowHeight)
        .then(
            if (repeatOnHold) {
                Modifier.repeatOnHold(
                    onHaptic = onHaptic,
                    onRepeat = onClick,
                    onShowHelp = onShowHelp,
                )
            } else {
                Modifier.combinedClickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = {
                        onHaptic()
                        onClick()
                    },
                    onLongClick = onShowHelp,
                )
            },
        )
    Box(
        modifier = keyModifier,
        contentAlignment = Alignment.Center,
    ) {
        if (label == "…") {
            Icon(
                Icons.Filled.MoreHoriz,
                contentDescription = "Scientific",
                tint = HermesColors.Foreground,
                modifier = Modifier.size(CalcSizing.utilityIconSize),
            )
        } else {
            Text(label, color = HermesColors.Foreground, style = CalcSizing.utilityText)
        }
    }
}

private fun Modifier.repeatOnHold(
    initialDelayMillis: Long = 400,
    repeatIntervalMillis: Long = 75,
    helpDelayMillis: Long = CALC_BUTTON_HELP_HOLD_MS,
    onHaptic: () -> Unit = {},
    onRepeat: () -> Unit,
    onShowHelp: () -> Unit = {},
): Modifier = pointerInput(onRepeat, onShowHelp) {
    awaitEachGesture {
        awaitFirstDown()
        onHaptic()
        onRepeat()

        val startTime = System.currentTimeMillis()
        var nextRepeatAt = startTime + initialDelayMillis
        var helpShown = false

        while (true) {
            val now = System.currentTimeMillis()
            if (!helpShown && now >= startTime + helpDelayMillis) {
                helpShown = true
                onShowHelp()
                break
            }

            val waitUntil = if (helpShown) {
                Long.MAX_VALUE
            } else {
                minOf(nextRepeatAt, startTime + helpDelayMillis)
            }
            val waitMs = (waitUntil - now).coerceAtLeast(1)
            val released = withTimeoutOrNull(waitMs) {
                waitForUpOrCancellation()
            }
            if (released != null) break

            if (!helpShown && System.currentTimeMillis() >= nextRepeatAt) {
                onRepeat()
                nextRepeatAt = System.currentTimeMillis() + repeatIntervalMillis
            }
        }
    }
}
