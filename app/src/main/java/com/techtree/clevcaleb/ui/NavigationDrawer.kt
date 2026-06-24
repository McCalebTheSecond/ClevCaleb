package com.techtree.clevcaleb.ui

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import com.techtree.clevcaleb.model.CalculatorRoute
import com.techtree.clevcaleb.theme.HermesColors
import kotlinx.coroutines.launch

@Composable
fun ClevCalcDrawer(
    favorites: List<CalculatorRoute>,
    allCalculators: List<CalculatorRoute>,
    onNavigate: (CalculatorRoute) -> Unit,
    onSettings: () -> Unit,
    onHelp: () -> Unit,
    content: @Composable (openDrawer: () -> Unit) -> Unit,
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val view = LocalView.current

    fun navigate(route: CalculatorRoute) {
        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
        scope.launch { drawerState.close() }
        onNavigate(route)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = HermesColors.Card,
                drawerContentColor = HermesColors.Foreground,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState())
                        .padding(vertical = 16.dp),
                ) {
                    DrawerAction(Icons.Filled.Settings, "Settings") {
                        scope.launch { drawerState.close() }
                        onSettings()
                    }
                    DrawerAction(Icons.Filled.Help, "Help") {
                        scope.launch { drawerState.close() }
                        onHelp()
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = HermesColors.Border)

                    DrawerSectionHeader("Favorites")
                    favorites.forEach { route ->
                        NavigationDrawerItem(
                            label = { Text(route.title) },
                            selected = false,
                            onClick = { navigate(route) },
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                            colors = NavigationDrawerItemDefaults.colors(
                                unselectedContainerColor = HermesColors.Card,
                                unselectedTextColor = HermesColors.Foreground,
                            ),
                        )
                    }

                    DrawerSectionHeader("All Calculators")
                    allCalculators.forEach { route ->
                        NavigationDrawerItem(
                            label = { Text(route.title) },
                            selected = false,
                            onClick = { navigate(route) },
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                            colors = NavigationDrawerItemDefaults.colors(
                                unselectedContainerColor = HermesColors.Card,
                                unselectedTextColor = HermesColors.Foreground,
                            ),
                        )
                    }
                }
            }
        },
    ) {
        content { scope.launch { drawerState.open() } }
    }
}

@Composable
private fun DrawerSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = HermesColors.NousBlue,
        modifier = Modifier.padding(horizontal = 28.dp, vertical = 8.dp),
    )
}

@Composable
private fun DrawerAction(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 14.dp),
    ) {
        Icon(icon, contentDescription = null, tint = HermesColors.Foreground)
        Spacer(Modifier.width(16.dp))
        Text(label, color = HermesColors.Foreground)
    }
}
