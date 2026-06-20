package com.braniik.slate.ui

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun SystemBarAppearance(
    statusBarForeground: Color,
    navigationBarForeground: Color
) {
    val view = LocalView.current
    if (view.isInEditMode) return

    val darkStatusIcons = statusBarForeground.luminance() < 0.5f
    val darkNavIcons = navigationBarForeground.luminance() < 0.5f

    SideEffect {
        val window = (view.context as Activity).window
        val controller = WindowCompat.getInsetsController(window, view)
        controller.isAppearanceLightStatusBars = darkStatusIcons
        controller.isAppearanceLightNavigationBars = darkNavIcons
    }
}