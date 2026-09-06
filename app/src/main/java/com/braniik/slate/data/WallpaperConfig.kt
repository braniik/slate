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

val LocalWallpaperTextColor = compositionLocalOf { LightText }
val LocalToolbarTextColor = compositionLocalOf { LightText }
val LocalWallpaperImage = compositionLocalOf<ImageBitmap?> { null }

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

fun WallpaperConfig.textColor(): Color = textColorAtFraction(0.5f)

fun WallpaperConfig.textColorAt(screenEdge: String): Color {
    val fraction = when (mode) {
        "gradient" -> gradientFractionAt(screenEdge, gradientDirection)
        else -> 0.5f
    }
    return textColorAtFraction(fraction)
}

private fun WallpaperConfig.textColorAtFraction(t: Float): Color {
    val representative = when (mode) {
        "image" -> Color(imageDominantColor)
        "gradient" -> lerpColor(Color(gradientStart), Color(gradientEnd), t)
        else -> Color(solidColor)
    }
    return foregroundFor(representative)
}

private fun lerpColor(a: Color, b: Color, t: Float): Color = Color(
    red = a.red + (b.red - a.red) * t,
    green = a.green + (b.green - a.green) * t,
    blue = a.blue + (b.blue - a.blue) * t
)

private fun gradientFractionAt(screenEdge: String, gradientDirection: String): Float =
    when (gradientDirection) {
        "top_to_bottom" -> when (screenEdge) {
            "top" -> 0.0f; "bottom" -> 1.0f; else -> 0.5f
        }
        "bottom_to_top" -> when (screenEdge) {
            "top" -> 1.0f; "bottom" -> 0.0f; else -> 0.5f
        }
        "left_to_right" -> when (screenEdge) {
            "left" -> 0.0f; "right" -> 1.0f; else -> 0.5f
        }
        "right_to_left" -> when (screenEdge) {
            "left" -> 1.0f; "right" -> 0.0f; else -> 0.5f
        }
        "top_left_to_bottom_right" -> when (screenEdge) {
            "top" -> 0.25f; "left" -> 0.25f; "bottom" -> 0.75f; "right" -> 0.75f; else -> 0.5f
        }
        "top_right_to_bottom_left" -> when (screenEdge) {
            "top" -> 0.25f; "right" -> 0.25f; "bottom" -> 0.75f; "left" -> 0.75f; else -> 0.5f
        }
        "bottom_left_to_top_right" -> when (screenEdge) {
            "bottom" -> 0.25f; "left" -> 0.25f; "top" -> 0.75f; "right" -> 0.75f; else -> 0.5f
        }
        "bottom_right_to_top_left" -> when (screenEdge) {
            "bottom" -> 0.25f; "right" -> 0.25f; "top" -> 0.75f; "left" -> 0.75f; else -> 0.5f
        }
        else -> 0.5f
    }

fun gradientAxisFractions(direction: String): FloatArray = when (direction) {
    "top_to_bottom" -> floatArrayOf(0f, 0f, 0f, 1f)
    "bottom_to_top" -> floatArrayOf(0f, 1f, 0f, 0f)
    "left_to_right" -> floatArrayOf(0f, 0f, 1f, 0f)
    "right_to_left" -> floatArrayOf(1f, 0f, 0f, 0f)
    "top_left_to_bottom_right" -> floatArrayOf(0f, 0f, 1f, 1f)
    "top_right_to_bottom_left" -> floatArrayOf(1f, 0f, 0f, 1f)
    "bottom_left_to_top_right" -> floatArrayOf(0f, 1f, 1f, 0f)
    "bottom_right_to_top_left" -> floatArrayOf(1f, 1f, 0f, 0f)
    else -> floatArrayOf(0f, 0f, 0f, 1f)
}

private fun directionOffsets(direction: String, size: Size): Pair<Offset, Offset> {
    val f = gradientAxisFractions(direction)
    return Offset(f[0] * size.width, f[1] * size.height) to
            Offset(f[2] * size.width, f[3] * size.height)
}