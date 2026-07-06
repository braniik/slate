package com.braniik.slate.data

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap

fun resolveEdgeForeground(
    wallpaper: WallpaperConfig,
    imageBitmap: ImageBitmap?,
    screenWidthPx: Int,
    screenHeightPx: Int,
    edge: String
): Color =
    if (wallpaper.mode == "image" && imageBitmap != null && screenWidthPx > 0 && screenHeightPx > 0) {
        sampleToolbarEdgeColor(imageBitmap, screenWidthPx, screenHeightPx, edge)
    } else {
        wallpaper.textColorAt(edge)
    }

fun sampleToolbarEdgeColor(
    bitmap: ImageBitmap,
    screenWidthPx: Int,
    screenHeightPx: Int,
    edge: String
): Color {
    if (screenWidthPx <= 0 || screenHeightPx <= 0) return LightText

    val scale = maxOf(
        screenWidthPx.toFloat() / bitmap.width,
        screenHeightPx.toFloat() / bitmap.height
    )
    val srcW = (screenWidthPx / scale).toInt()
    val srcH = (screenHeightPx / scale).toInt()
    val srcX = (bitmap.width - srcW) / 2
    val srcY = (bitmap.height - srcH) / 2
    val strip = when (edge) {
        "left", "right" -> maxOf(1, srcW / 20)
        else -> maxOf(1, srcH / 20)
    }
    val sx: Int; val sy: Int; val sw: Int; val sh: Int
    when (edge) {
        "bottom" -> { sx = srcX; sy = srcY + srcH - strip; sw = srcW; sh = strip }
        "left" -> { sx = srcX; sy = srcY; sw = strip; sh = srcH }
        "right" -> { sx = srcX + srcW - strip; sy = srcY; sw = strip; sh = srcH }
        else -> { sx = srcX; sy = srcY; sw = srcW; sh = strip }
    }
    val cx = sx.coerceIn(0, bitmap.width - 1)
    val cy = sy.coerceIn(0, bitmap.height - 1)
    val cw = sw.coerceIn(1, bitmap.width - cx)
    val ch = sh.coerceIn(1, bitmap.height - cy)

    return try {
        val pixels = IntArray(cw * ch)
        bitmap.readPixels(pixels, cx, cy, cw, ch)
        foregroundForLuminance(averageLuminance(pixels))
    } catch (_: Exception) {
        LightText
    }
}

private fun averageLuminance(pixels: IntArray): Float {
    if (pixels.isEmpty()) return 0f
    var sum = 0f
    for (argb in pixels) sum += relativeLuminance(argb)
    return sum / pixels.size
}
