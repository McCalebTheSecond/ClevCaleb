package com.techtree.clevcaleb.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.techtree.clevcaleb.model.CalculatorRoute
import com.techtree.clevcaleb.ui.AppViewModel
import com.techtree.clevcaleb.ui.ClevCalcDrawer
import com.techtree.clevcaleb.ui.calculators.CurrencyConverterScreen
import com.techtree.clevcaleb.ui.calculators.DateScreen
import com.techtree.clevcaleb.ui.calculators.DiscountScreen
import com.techtree.clevcaleb.ui.calculators.FuelCostScreen
import com.techtree.clevcaleb.ui.calculators.FuelEfficiencyScreen
import com.techtree.clevcaleb.ui.calculators.GpaScreen
import com.techtree.clevcaleb.ui.calculators.HealthScreen
import com.techtree.clevcaleb.ui.calculators.HexScreen
import com.techtree.clevcaleb.ui.calculators.LoanScreen
import com.techtree.clevcaleb.ui.calculators.MainCalculatorScreen
import com.techtree.clevcaleb.ui.calculators.OvulationScreen
import com.techtree.clevcaleb.ui.calculators.PercentageScreen
import com.techtree.clevcaleb.ui.calculators.SalesTaxScreen
import com.techtree.clevcaleb.ui.calculators.SavingsScreen
import com.techtree.clevcaleb.ui.calculators.TipScreen
import com.techtree.clevcaleb.ui.calculators.UnitConverterScreen
import com.techtree.clevcaleb.ui.calculators.UnitPriceScreen
import com.techtree.clevcaleb.ui.calculators.WorldTimeScreen
import com.techtree.clevcaleb.ui.settings.CalculatorListScreen
import com.techtree.clevcaleb.ui.settings.HelpScreen
import com.techtree.clevcaleb.ui.settings.SettingsScreen
import com.techtree.clevcaleb.ui.settings.StartupCalculatorScreen

@Composable
fun ClevCalebApp(viewModel: AppViewModel) {
    val navController = rememberNavController()
    val favorites by viewModel.favorites.collectAsState()
    val visible by viewModel.visibleCalculators.collectAsState()
    val startup by viewModel.startupCalculator.collectAsState()
    val openListAtStartup by viewModel.openListAtStartup.collectAsState()
    var handledStartup by remember { mutableStateOf(false) }

    LaunchedEffect(handledStartup, startup, openListAtStartup) {
        if (!handledStartup) {
            handledStartup = true
            if (openListAtStartup) {
                // Drawer opens on first frame via user; stay on main for now
            } else if (startup != CalculatorRoute.Main) {
                navController.navigate(startup.route) {
                    popUpTo(CalculatorRoute.Main.route) { inclusive = false }
                }
            }
        }
    }

    ClevCalcDrawer(
        favorites = favorites.ifEmpty {
            listOf(CalculatorRoute.Main, CalculatorRoute.FuelCost, CalculatorRoute.Percentage)
        },
        allCalculators = visible,
        onNavigate = { route ->
            navController.navigate(route.route) {
                launchSingleTop = true
            }
        },
        onSettings = { navController.navigate("settings") },
        onHelp = { navController.navigate("help") },
    ) { openDrawer ->
        NavHost(
            navController = navController,
            startDestination = CalculatorRoute.Main.route,
        ) {
            composable(CalculatorRoute.Main.route) {
                MainCalculatorScreen(
                    viewModel = viewModel,
                    onOpenDrawer = openDrawer,
                    onSettings = { navController.navigate("settings") },
                )
            }
            calculatorScreen(CalculatorRoute.UnitConverter.route) { UnitConverterScreen { navController.popBackStack() } }
            calculatorScreen(CalculatorRoute.Currency.route) { CurrencyConverterScreen { navController.popBackStack() } }
            calculatorScreen(CalculatorRoute.Percentage.route) { PercentageScreen { navController.popBackStack() } }
            calculatorScreen(CalculatorRoute.Discount.route) { DiscountScreen { navController.popBackStack() } }
            calculatorScreen(CalculatorRoute.Loan.route) { LoanScreen { navController.popBackStack() } }
            calculatorScreen(CalculatorRoute.Date.route) { DateScreen { navController.popBackStack() } }
            calculatorScreen(CalculatorRoute.Health.route) { HealthScreen { navController.popBackStack() } }
            calculatorScreen(CalculatorRoute.FuelCost.route) { FuelCostScreen { navController.popBackStack() } }
            calculatorScreen(CalculatorRoute.FuelEfficiency.route) { FuelEfficiencyScreen { navController.popBackStack() } }
            calculatorScreen(CalculatorRoute.Gpa.route) { GpaScreen { navController.popBackStack() } }
            calculatorScreen(CalculatorRoute.Tip.route) { TipScreen { navController.popBackStack() } }
            calculatorScreen(CalculatorRoute.SalesTax.route) { SalesTaxScreen { navController.popBackStack() } }
            calculatorScreen(CalculatorRoute.UnitPrice.route) { UnitPriceScreen { navController.popBackStack() } }
            calculatorScreen(CalculatorRoute.WorldTime.route) { WorldTimeScreen { navController.popBackStack() } }
            calculatorScreen(CalculatorRoute.Ovulation.route) { OvulationScreen { navController.popBackStack() } }
            calculatorScreen(CalculatorRoute.Hex.route) { HexScreen { navController.popBackStack() } }
            calculatorScreen(CalculatorRoute.Savings.route) { SavingsScreen { navController.popBackStack() } }

            composable("settings") {
                SettingsScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onCalculatorList = { navController.navigate("calculator_list") },
                    onStartupCalculator = { navController.navigate("startup_calculator") },
                )
            }
            composable("calculator_list") {
                CalculatorListScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
            }
            composable("startup_calculator") {
                StartupCalculatorScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
            }
            composable("help") {
                HelpScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}

private fun androidx.navigation.NavGraphBuilder.calculatorScreen(
    route: String,
    content: @Composable () -> Unit,
) {
    composable(route) { content() }
}
