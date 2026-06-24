package com.techtree.clevcaleb.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.techtree.clevcaleb.model.CalculatorRoute
import com.techtree.clevcaleb.theme.HermesColors
import com.techtree.clevcaleb.ui.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: AppViewModel,
    onBack: () -> Unit,
    onCalculatorList: () -> Unit,
    onStartupCalculator: () -> Unit,
) {
    val startup by viewModel.startupCalculator.collectAsState()
    val vibration by viewModel.buttonVibration.collectAsState()
    val keepScreenOn by viewModel.keepScreenOn.collectAsState()
    val keepRecord by viewModel.keepCalcRecord.collectAsState()
    val openList by viewModel.openListAtStartup.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = HermesColors.Sidebar,
                    titleContentColor = HermesColors.Foreground,
                    navigationIconContentColor = HermesColors.Foreground,
                ),
            )
        },
        containerColor = HermesColors.Background,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
        ) {
            SectionHeader("General")
            SettingsNavItem("Calculator list", "Edit the list of favorite calculators", onCalculatorList)
            SettingsNavItem("Startup calculator", startup.title, onStartupCalculator)
            SettingsNavItem("Theme", "Hermes Nous Blue Dark", onClick = {})
            SettingsNavItem("Number format", "US English (1,234.56)", onClick = {})

            SectionHeader("Details")
            SettingsToggle("Button feedback", "Vibration", vibration) { viewModel.setButtonVibration(it) }
            SettingsToggle("Keep screen on", "Always keep the screen on", keepScreenOn) { viewModel.setKeepScreenOn(it) }
            SettingsToggle("Keep the calculation record", "Keep the last calculation record", keepRecord) {
                viewModel.setKeepCalcRecord(it)
            }
            SettingsToggle("Open calculator list", "Open the list at startup", openList) {
                viewModel.setOpenListAtStartup(it)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorListScreen(
    viewModel: AppViewModel,
    onBack: () -> Unit,
) {
    val favorites by viewModel.favorites.collectAsState()
    val visible by viewModel.visibleCalculators.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Calculator list") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = HermesColors.Sidebar,
                    titleContentColor = HermesColors.Foreground,
                    navigationIconContentColor = HermesColors.Foreground,
                ),
            )
        },
        containerColor = HermesColors.Background,
    ) { padding ->
        Column(modifier = Modifier.padding(padding).verticalScroll(rememberScrollState())) {
            SectionHeader("Favorites")
            CalculatorRoute.entries.forEach { route ->
                val checked = route in favorites
                SettingsToggle(route.title, if (checked) "In favorites" else "Not in favorites", checked) { enabled ->
                    val next = if (enabled) favorites + route else favorites - route
                    viewModel.setFavorites(next.distinct())
                }
            }
            HorizontalDivider(color = HermesColors.Border, modifier = Modifier.padding(vertical = 8.dp))
            SectionHeader("Visible calculators")
            CalculatorRoute.entries.forEach { route ->
                val checked = route in visible
                SettingsToggle(route.title, if (checked) "Shown in drawer" else "Hidden", checked) { enabled ->
                    val next = if (enabled) visible + route else visible - route
                    viewModel.setVisibleCalculators(next.distinct().ifEmpty { listOf(CalculatorRoute.Main) })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartupCalculatorScreen(viewModel: AppViewModel, onBack: () -> Unit) {
    val startup by viewModel.startupCalculator.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Startup calculator") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = HermesColors.Sidebar,
                    titleContentColor = HermesColors.Foreground,
                    navigationIconContentColor = HermesColors.Foreground,
                ),
            )
        },
        containerColor = HermesColors.Background,
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            CalculatorRoute.entries.forEach { route ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.setStartupCalculator(route); onBack() }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = route.title,
                        color = if (route == startup) HermesColors.Primary else HermesColors.Foreground,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Help") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = HermesColors.Sidebar,
                    titleContentColor = HermesColors.Foreground,
                    navigationIconContentColor = HermesColors.Foreground,
                ),
            )
        },
        containerColor = HermesColors.Background,
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Text("ClevCaleb Help", style = MaterialTheme.typography.titleMedium, color = HermesColors.Foreground)
            Text(
                "Tap the menu icon to open all calculators. Use the history icon on the main calculator to review past calculations. Configure favorites and startup calculator in Settings. This US English build uses imperial defaults and US time zones.",
                color = HermesColors.MutedForeground,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = HermesColors.MutedForeground,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
    )
}

@Composable
private fun SettingsNavItem(title: String, subtitle: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Text(title, color = HermesColors.Foreground, style = MaterialTheme.typography.bodyLarge)
        Text(subtitle, color = HermesColors.MutedForeground, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun SettingsToggle(title: String, subtitle: String, checked: Boolean, onChecked: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onChecked(!checked) }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = HermesColors.Foreground, style = MaterialTheme.typography.bodyLarge)
            Text(subtitle, color = HermesColors.MutedForeground, style = MaterialTheme.typography.bodySmall)
        }
        Checkbox(
            checked = checked,
            onCheckedChange = onChecked,
            colors = CheckboxDefaults.colors(checkedColor = HermesColors.NousBlue),
        )
    }
}
