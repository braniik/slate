package com.braniik.slate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.braniik.slate.data.launcherSettingsFlow
import com.braniik.slate.data.saveSettings
import com.braniik.slate.ui.drawer.AppDrawerScreen
import com.braniik.slate.ui.setup.SetupScreen
import com.braniik.slate.ui.theme.SlateTheme
import kotlinx.coroutines.launch

private val Background = Color(0xFF080808)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SlateTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Background)
                ) {
                    SlateApp()
                }
            }
        }
    }
}

@Composable
fun SlateApp() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val settings by context.launcherSettingsFlow().collectAsState(initial = null)

    when {
        settings == null -> {
        }
        !settings!!.setupDone -> {
            SetupScreen { chosenSettings ->
                scope.launch {
                    context.saveSettings(chosenSettings)
                }
            }
        }
        else -> {
            AppDrawerScreen(settings = settings!!)
        }
    }
}