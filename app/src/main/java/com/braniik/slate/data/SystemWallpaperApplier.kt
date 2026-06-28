package com.braniik.slate.data

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Shader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

suspend fun applySystemWallpaper(context: Context, config: WallpaperConfig) =
    withContext(Dispatchers.IO) {
        val metrics = context.resources.displayMetrics
        val width = metrics.widthPixels
        val height = metrics.heightPixels
        if (width <= 0 || height <= 0) return@withContext

        val bitmap = try {
            renderWallpaperBitmap(config, width, height)
        } catch (_: Throwable) {
            return@withContext
        }

        try {
            WallpaperManager.getInstance(context).setBitmap(
                bitmap,
                null,
                true,
                WallpaperManager.FLAG_SYSTEM
            )
        } catch (_: Exception) {} finally {
            bitmap.recycle()
        }
    }

private fun renderWallpaperBitmap(config: WallpaperConfig, width: Int, height: Int): Bitmap {
    val out = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(out)
    when (config.mode) {
        "image" -> {
            val src = decodeWallpaperFile(config.imagePath)
            if (src == null) {
                canvas.drawColor(config.solidColor)
            } else {
                drawCenterCropped(canvas, src, width, height)
                src.recycle()
            }
        }
        "gradient" -> {
            val f = gradientAxisFractions(config.gradientDirection)
            val paint = Paint().apply {
                shader = LinearGradient(
                    f[0] * width, f[1] * height,
                    f[2] * width, f[3] * height,
                    config.gradientStart, config.gradientEnd,
                    Shader.TileMode.CLAMP
                )
            }
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        }
        else -> canvas.drawColor(config.solidColor)
    }
    return out
}

private fun drawCenterCropped(canvas: Canvas, src: Bitmap, width: Int, height: Int) {
    val scale = maxOf(width.toFloat() / src.width, height.toFloat() / src.height)
    val srcW = (width / scale).toInt()
    val srcH = (height / scale).toInt()
    val srcX = (src.width - srcW) / 2
    val srcY = (src.height - srcH) / 2
    canvas.drawBitmap(
        src,
        Rect(srcX, srcY, srcX + srcW, srcY + srcH),
        Rect(0, 0, width, height),
        null
    )
}

private fun decodeWallpaperFile(path: String): Bitmap? {
    if (path.isBlank()) return null
    if (!File(path).exists()) return null
    return try {
        BitmapFactory.decodeFile(path)
    } catch (_: Exception) {
        null
    }
}