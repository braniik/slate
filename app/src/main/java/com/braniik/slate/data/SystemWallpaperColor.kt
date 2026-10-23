package com.braniik.slate.data

import android.app.WallpaperColors
import android.app.WallpaperManager
import android.content.Context
import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn

fun Context.systemWallpaperColorFlow(): Flow<Int?> = callbackFlow {
    val manager = WallpaperManager.getInstance(this@systemWallpaperColorFlow)

    trySend(manager.getWallpaperColors(WallpaperManager.FLAG_SYSTEM).primaryArgb())

    val listener = WallpaperManager.OnColorsChangedListener { colors, which ->
        if ((which and WallpaperManager.FLAG_SYSTEM) != 0) trySend(colors.primaryArgb())
    }
    manager.addOnColorsChangedListener(listener, Handler(Looper.getMainLooper()))
    awaitClose { manager.removeOnColorsChangedListener(listener) }
}.flowOn(Dispatchers.IO)

private fun WallpaperColors?.primaryArgb(): Int? = this?.primaryColor?.toArgb()