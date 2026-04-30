package com.braniik.slate.data

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import org.json.JSONObject

private val LightText = Color(0xFFFFFFFF)
private val DarkText = Color(0xFF111111)

val LocalWallpaperTextColor = compositionLocalOf { LightText }

data class WallpaperConfig(
    val mode: String = "solid",
    val solidColor: Int = 0xFF080808.toInt(),
    val gradientStart: Int = 0xFF080808.toInt(),
    val gradientEnd: Int = 0xFF333333.toInt(),
    val gradientDirection: String = "top_to_bottom",
    val imagePath: String = "",
    val imageDominantColor: Int = 0xFF333333.toInt()
) {
    fun toJson(): String = JSONObject().apply {
        put("mode", mode)
        put("solidColor", solidColor)
        put("gradientStart", gradientStart)
        put("gradientEnd", gradientEnd)
        put("gradientDirection", gradientDirection)
        put("imagePath", imagePath)
        put("imageDominantColor", imageDominantColor)
    }.toString()

    companion object {
        fun fromJson(raw: String): WallpaperConfig = try {
            val o = JSONObject(raw)
            WallpaperConfig(
                mode = o.optString("mode", "solid"),
                solidColor = o.optInt("solidColor", 0xFF080808.toInt()),
                gradientStart = o.optInt("gradientStart", 0xFF080808.toInt()),
                gradientEnd = o.optInt("gradientEnd", 0xFF333333.toInt()),
                gradientDirection = o.optString("gradientDirection", "top_to_bottom"),
                imagePath = o.optString("imagePath", ""),
                imageDominantColor = o.optInt("imageDominantColor", 0xFF333333.toInt())
            )
        } catch (_: Exception) {
            WallpaperConfig()
        }
    }
}

fun Modifier.wallpaperBackground(
    config: WallpaperConfig,
    imageBitmap: ImageBitmap? = null
): Modifier = drawBehind {
    when (config.mode) {
        "image" -> {
            imageBitmap?.let { bmp ->
                val scale = maxOf(
                    size.width / bmp.width.toFloat(),
                    size.height / bmp.height.toFloat()
                )
                val srcW = (size.width / scale).toInt()
                val srcH = (size.height / scale).toInt()
                val srcX = (bmp.width - srcW) / 2
                val srcY = (bmp.height - srcH) / 2
                drawImage(
                    image = bmp,
                    srcOffset = IntOffset(srcX, srcY),
                    srcSize = IntSize(srcW, srcH),
                    dstSize = IntSize(size.width.toInt(), size.height.toInt())
                )
            } ?: drawRect(color = Color(config.solidColor))
        }
        "gradient" -> {
            val colors = listOf(Color(config.gradientStart), Color(config.gradientEnd))
            val (start, end) = directionOffsets(config.gradientDirection, size)
            drawRect(brush = Brush.linearGradient(colors, start, end))
        }
        else -> drawRect(color = Color(config.solidColor))
    }
}

fun WallpaperConfig.textColor(): Color {
    val representative = when (mode) {
        "image" -> Color(imageDominantColor)
        "gradient" -> {
            val a = Color(gradientStart)
            val b = Color(gradientEnd)
            Color(
                red = (a.red + b.red) / 2f,
                green = (a.green + b.green) / 2f,
                blue = (a.blue + b.blue) / 2f
            )
        }
        else -> Color(solidColor)
    }
    val luminance = 0.299f * representative.red + 0.587f * representative.green + 0.114f * representative.blue
    return if (luminance > 0.5f) DarkText else LightText
}

private fun directionOffsets(direction: String, size: Size): Pair<Offset, Offset> =
    when (direction) {
        "top_to_bottom" -> Offset(0f, 0f) to Offset(0f, size.height)
        "bottom_to_top" -> Offset(0f, size.height) to Offset(0f, 0f)
        "left_to_right" -> Offset(0f, 0f) to Offset(size.width, 0f)
        "right_to_left" -> Offset(size.width, 0f) to Offset(0f, 0f)
        "top_left_to_bottom_right" -> Offset(0f, 0f) to Offset(size.width, size.height)
        "top_right_to_bottom_left" -> Offset(size.width, 0f) to Offset(0f, size.height)
        "bottom_left_to_top_right" -> Offset(0f, size.height) to Offset(size.width, 0f)
        "bottom_right_to_top_left" -> Offset(size.width, size.height) to Offset(0f, 0f)
        else -> Offset(0f, 0f) to Offset(0f, size.height)
    }