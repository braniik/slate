package com.braniik.slate.data

import android.app.WallpaperColors
import android.app.WallpaperManager
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn

fun Context.systemWallpaperDarkTextFlow(): Flow<Boolean?> = callbackFlow {
    val manager = WallpaperManager.getInstance(this@systemWallpaperDarkTextFlow)

    trySend(manager.getWallpaperColors(WallpaperManager.FLAG_SYSTEM).prefersDarkText())

    val listener = WallpaperManager.OnColorsChangedListener { colors, which ->
        if ((which and WallpaperManager.FLAG_SYSTEM) != 0) trySend(colors.prefersDarkText())
    }
    manager.addOnColorsChangedListener(listener, Handler(Looper.getMainLooper()))
    awaitClose { manager.removeOnColorsChangedListener(listener) }
}.flowOn(Dispatchers.IO)

private fun WallpaperColors?.prefersDarkText(): Boolean? = when {
    this == null -> null
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
        (colorHints and WallpaperColors.HINT_SUPPORTS_DARK_TEXT) != 0
    else -> foregroundFor(Color(primaryColor.toArgb())) == DarkText
}