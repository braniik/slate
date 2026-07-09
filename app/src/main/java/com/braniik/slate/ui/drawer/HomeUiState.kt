package com.braniik.slate.ui.drawer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

/**
 * Ephemeral UI state of the home screen: the active mode and
 * whichever overlay is currently open.
 * Layer order, topmost first:
 * wallpaper picker -> blanket set -> per-app edit dialog -> settings sheet -> mode.
 */
class HomeUiState {
    var mode by mutableStateOf(HomeMode.NORMAL)
    var showSettings by mutableStateOf(false)
    var showBlanketSet by mutableStateOf(false)
    var showWallpaperPicker by mutableStateOf(false)
    var editingPkg by mutableStateOf<String?>(null)

    fun dismissTopmost() {
        when {
            showWallpaperPicker -> showWallpaperPicker = false
            showBlanketSet -> showBlanketSet = false
            editingPkg != null -> editingPkg = null
            showSettings -> showSettings = false
            mode != HomeMode.NORMAL -> mode = HomeMode.NORMAL
        }
    }

    fun reset() {
        showWallpaperPicker = false
        showBlanketSet = false
        editingPkg = null
        showSettings = false
        mode = HomeMode.NORMAL
    }
}

@Composable
fun rememberHomeUiState(): HomeUiState = remember { HomeUiState() }