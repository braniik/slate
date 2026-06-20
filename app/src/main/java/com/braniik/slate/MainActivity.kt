package com.braniik.slate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntSize
import com.braniik.slate.data.LocalToolbarTextColor
import com.braniik.slate.data.LocalWallpaperTextColor
import com.braniik.slate.data.WallpaperConfig
import com.braniik.slate.data.launcherSettingsFlow
import com.braniik.slate.data.loadWallpaperBitmap
import com.braniik.slate.data.resolveEdgeForeground
import com.braniik.slate.data.saveSettings
import com.braniik.slate.data.textColor
import com.braniik.slate.data.wallpaperBackground
import com.braniik.slate.data.wallpaperConfigFlow
import com.braniik.slate.ui.SystemBarAppearance
import com.braniik.slate.ui.drawer.AppDrawerScreen
import com.braniik.slate.ui.setup.SetupScreen
import com.braniik.slate.ui.theme.SlateTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SlateTheme {
                SlateApp()
            }
        }
    }
}

@Composable
fun SlateApp() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val settings by context.launcherSettingsFlow().collectAsState(initial = null)
    val wallpaper by context.wallpaperConfigFlow().collectAsState(initial = WallpaperConfig())

    val imageBitmap = remember(wallpaper.mode, wallpaper.imagePath) {
        if (wallpaper.mode == "image" && wallpaper.imagePath.isNotBlank())
            loadWallpaperBitmap(wallpaper.imagePath)
        else null
    }

    val toolbarPos = settings?.toolbarPosition ?: "top"
    var screenSize by remember { mutableStateOf(IntSize.Zero) }

    val toolbarTextColor = remember(wallpaper, imageBitmap, toolbarPos, screenSize) {
        resolveEdgeForeground(wallpaper, imageBitmap, screenSize.width, screenSize.height, toolbarPos)
    }

    val statusBarForeground = remember(wallpaper, imageBitmap, screenSize) {
        resolveEdgeForeground(wallpaper, imageBitmap, screenSize.width, screenSize.height, "top")
    }
    val navBarForeground = remember(wallpaper, imageBitmap, screenSize) {
        resolveEdgeForeground(wallpaper, imageBitmap, screenSize.width, screenSize.height, "bottom")
    }

    SystemBarAppearance(statusBarForeground, navBarForeground)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { screenSize = it }
            .wallpaperBackground(wallpaper, imageBitmap)
    ) {
        CompositionLocalProvider(
            LocalWallpaperTextColor provides wallpaper.textColor(),
            LocalToolbarTextColor provides toolbarTextColor
        ) {
            when {
                settings == null -> {}
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
    }
}