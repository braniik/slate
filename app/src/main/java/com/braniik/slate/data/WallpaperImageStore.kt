package com.braniik.slate.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.palette.graphics.Palette
import java.io.File

private const val WALLPAPER_FILE = "wallpaper.webp"
private const val MAX_DIMENSION = 2560

data class WallpaperColors(
    val dominant: Int
)

fun saveWallpaperImage(context: Context, uri: Uri): String? {
    val dest = File(context.filesDir, WALLPAPER_FILE)
    return try {
        context.contentResolver.openInputStream(uri)?.use { input ->
            var bitmap = BitmapFactory.decodeStream(input) ?: return null

            val longest = maxOf(bitmap.width, bitmap.height)
            if (longest > MAX_DIMENSION) {
                val scale = MAX_DIMENSION.toFloat() / longest
                val scaled = Bitmap.createScaledBitmap(
                    bitmap,
                    (bitmap.width * scale).toInt(),
                    (bitmap.height * scale).toInt(),
                    true
                )
                bitmap.recycle()
                bitmap = scaled
            }

            dest.outputStream().use { output -> compressBitmap(bitmap, output) }
            bitmap.recycle()
        }
        dest.absolutePath
    } catch (_: Exception) {
        null
    }
}

fun extractWallpaperColors(path: String): WallpaperColors {
    val fallback = WallpaperColors(0xFF333333.toInt())
    val file = File(path)
    if (!file.exists()) return fallback

    val bitmap = BitmapFactory.decodeFile(path) ?: return fallback
    val palette = Palette.from(bitmap).generate()
    bitmap.recycle()

    val dominant = palette.getDominantSwatch()?.rgb ?: fallback.dominant
    return WallpaperColors(dominant)
}

fun loadWallpaperBitmap(path: String): ImageBitmap? {
    if (path.isBlank()) return null
    val file = File(path)
    if (!file.exists()) return null
    return try {
        BitmapFactory.decodeFile(path)?.asImageBitmap()
    } catch (_: Exception) {
        null
    }
}

@Suppress("DEPRECATION")
private fun compressBitmap(bitmap: Bitmap, output: java.io.OutputStream) {
    val format = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
        Bitmap.CompressFormat.WEBP_LOSSY
    else
        Bitmap.CompressFormat.WEBP
    bitmap.compress(format, 85, output)
}