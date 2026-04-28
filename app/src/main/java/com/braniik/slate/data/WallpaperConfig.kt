package com.braniik.slate.data

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import org.json.JSONObject

private val LightText = Color(0xFFFFFFFF)
private val DarkText = Color(0xFF111111)

val LocalWallpaperTextColor = compositionLocalOf { LightText }

data class WallpaperConfig(
    val mode: String = "solid",
    val solidColor: Int = 0xFF080808.toInt(),
    val gradientStart: Int = 0xFF080808.toInt(),
    val gradientEnd: Int = 0xFF333333.toInt(),
    val gradientDirection: String = "top_to_bottom"
) {
    fun toJson(): String = JSONObject().apply {
        put("mode", mode)
        put("solidColor", solidColor)
        put("gradientStart", gradientStart)
        put("gradientEnd", gradientEnd)
        put("gradientDirection", gradientDirection)
    }.toString()

    companion object {
        fun fromJson(raw: String): WallpaperConfig = try {
            val o = JSONObject(raw)
            WallpaperConfig(
                mode = o.optString("mode", "solid"),
                solidColor = o.optInt("solidColor", 0xFF080808.toInt()),
                gradientStart = o.optInt("gradientStart", 0xFF080808.toInt()),
                gradientEnd = o.optInt("gradientEnd", 0xFF333333.toInt()),
                gradientDirection = o.optString("gradientDirection", "top_to_bottom")
            )
        } catch (_: Exception) {
            WallpaperConfig()
        }
    }
}

fun Modifier.wallpaperBackground(config: WallpaperConfig): Modifier = drawBehind {
    when (config.mode) {
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