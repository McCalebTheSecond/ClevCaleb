package com.techtree.clevcaleb

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.techtree.clevcaleb.data.AppPreferences
import com.techtree.clevcaleb.navigation.ClevCalebApp
import com.techtree.clevcaleb.theme.ClevCalebTheme
import com.techtree.clevcaleb.theme.HermesColors
import com.techtree.clevcaleb.ui.rememberAppViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val prefs by lazy { AppPreferences(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                prefs.keepScreenOn.collect { enabled ->
                    if (enabled) {
                        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                    } else {
                        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                    }
                }
            }
        }
        setContent {
            val viewModel = rememberAppViewModel(prefs)

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
