package com.techtree.clevcaleb.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.techtree.clevcaleb.theme.HermesColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScaffold(
    title: String,
    onBack: (() -> Unit)?,
    content: @Composable () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
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
                .padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            content()
        }
    }
}

@Composable
fun ResultCard(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .padding(vertical = 4.dp)
            .padding(12.dp),
    ) {
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = HermesColors.MutedForeground)
        Text(text = value, style = MaterialTheme.typography.titleLarge, color = HermesColors.Foreground)
    }
}
