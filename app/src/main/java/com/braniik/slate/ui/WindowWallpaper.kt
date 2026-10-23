package com.braniik.slate.ui

import android.app.Activity
import android.app.WallpaperManager
import android.graphics.drawable.ColorDrawable
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import com.braniik.slate.ui.theme.SlateBackground

@Composable
fun WindowWallpaper(showSystemWallpaper: Boolean) {
    val view = LocalView.current
    if (view.isInEditMode) return

    SideEffect {
        val window = (view.context as Activity).window
        if (showSystemWallpaper) {
            window.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
            window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER)
            view.windowToken?.let { token ->
                runCatching { WallpaperManager.getInstance(view.context).setWallpaperOffsets(token, 0.5f, 0.5f) }
            }
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER)
            window.setBackgroundDrawable(ColorDrawable(SlateBackground.toArgb()))
        }
    }
}