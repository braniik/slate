package com.braniik.slate.ui.drawer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

/**
 * Ephemeral UI state of the home screen: the active mode, whichever overlay is
 * currently open, and what edit mode is currently pointed at.
 * Layer order, topmost first:
 * wallpaper picker -> blanket set -> per-app edit dialog -> settings sheet ->
 * guide patterns -> selection -> mode.
 */
class HomeUiState {
    var mode by mutableStateOf(HomeMode.NORMAL)
    var showSettings by mutableStateOf(false)
    var showBlanketSet by mutableStateOf(false)
    var showWallpaperPicker by mutableStateOf(false)
    var editingKey by mutableStateOf<String?>(null)

    // Keys of the apps a marquee or a tap has picked out.
    var selection by mutableStateOf<Set<String>>(emptySet())

    // Edit mode sub-state: building guide lines by halving regions.
    var patternMode by mutableStateOf(false)

    fun selectMode(next: HomeMode) {
        showSettings = false
        mode = if (mode == next) HomeMode.NORMAL else next
        if (mode != HomeMode.EDITING) leaveEditing()
    }

    fun toggleSettings() {
        showSettings = !showSettings
        if (showSettings) {
            mode = HomeMode.NORMAL
            leaveEditing()
        }
    }

    fun togglePattern() {
        patternMode = !patternMode
        if (patternMode) selection = emptySet()
    }

    fun toggleSelected(key: String) {
        selection = if (key in selection) selection - key else selection + key
    }

    fun dismissTopmost() {
        when {
            showWallpaperPicker -> showWallpaperPicker = false
            showBlanketSet -> showBlanketSet = false
            editingKey != null -> editingKey = null
            showSettings -> showSettings = false
            patternMode -> patternMode = false
            selection.isNotEmpty() -> selection = emptySet()
            mode != HomeMode.NORMAL -> mode = HomeMode.NORMAL
        }
    }

    fun reset() {
        showWallpaperPicker = false
        showBlanketSet = false
        editingKey = null
        showSettings = false
        leaveEditing()
        mode = HomeMode.NORMAL
    }

    private fun leaveEditing() {
        selection = emptySet()
        patternMode = false
    }
}

@Composable
fun rememberHomeUiState(): HomeUiState = remember { HomeUiState() }