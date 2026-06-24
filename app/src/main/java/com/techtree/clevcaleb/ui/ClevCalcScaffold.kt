package com.techtree.clevcaleb.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.techtree.clevcaleb.theme.HermesColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClevCalcScaffold(
    title: String = "ClevCaleb",
    onOpenDrawer: () -> Unit,
    onHistory: (() -> Unit)? = null,
    onSettings: (() -> Unit)? = null,
    onDecimalPlaces: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Filled.Menu, contentDescription = "Menu")
                    }
                },
                actions = {
                    if (onHistory != null) {
                        IconButton(onClick = onHistory) {
                            Icon(Icons.Filled.History, contentDescription = "History")
                        }
                    }
                    if (onSettings != null || onDecimalPlaces != null) {
                        Box {
                            IconButton(onClick = { menuExpanded = true }) {
                                Icon(Icons.Filled.MoreVert, contentDescription = "More")
                            }
                            DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                                onDecimalPlaces?.let {
                                    DropdownMenuItem(
                                        text = { Text("Decimal places") },
                                        onClick = { menuExpanded = false; it() },
                                    )
                                }
                                onSettings?.let {
                                    DropdownMenuItem(
                                        text = { Text("Settings") },
                                        onClick = { menuExpanded = false; it() },
                                    )
                                }
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = HermesColors.Sidebar,
                    titleContentColor = HermesColors.Foreground,
                    navigationIconContentColor = HermesColors.Foreground,
                    actionIconContentColor = HermesColors.Foreground,
                ),
            )
        },
        containerColor = HermesColors.Background,
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            content()
        }
    }
}
