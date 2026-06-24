package com.techtree.clevcaleb

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.techtree.clevcaleb.data.AppPreferences
import com.techtree.clevcaleb.navigation.ClevCalebApp
import com.techtree.clevcaleb.theme.ClevCalebTheme
import com.techtree.clevcaleb.theme.HermesColors
import com.techtree.clevcaleb.ui.rememberAppViewModel

class MainActivity : ComponentActivity() {
    private val prefs by lazy { AppPreferences(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel = rememberAppViewModel(prefs)
            val keepScreenOn by viewModel.keepScreenOn.collectAsState()

            LaunchedEffect(keepScreenOn) {
                if (keepScreenOn) {
                    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                } else {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                }
            }

            ClevCalebTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = HermesColors.Background,
                ) {
                    ClevCalebApp(viewModel)
                }
            }
        }
    }
}
