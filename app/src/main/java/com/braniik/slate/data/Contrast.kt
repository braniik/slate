package com.braniik.slate.data

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import kotlin.math.pow

internal val LightText = Color(0xFFFFFFFF)
internal val DarkText = Color(0xFF111111)
private const val DARK_TEXT_LUMINANCE = 0.27f

private val srgbToLinear = FloatArray(256) { i ->
    val c = i / 255f
    if (c <= 0.04045f) c / 12.92f
    else ((c + 0.055f) / 1.055f).pow(2.4f)
}

internal fun relativeLuminance(argb: Int): Float {
    val r = srgbToLinear[argb shr 16 and 0xFF]
    val g = srgbToLinear[argb shr 8 and 0xFF]
    val b = srgbToLinear[argb and 0xFF]
    return 0.2126f * r + 0.7152f * g + 0.0722f * b
}

internal fun foregroundForLuminance(luminance: Float): Color =
    if (luminance > DARK_TEXT_LUMINANCE) DarkText else LightText

internal fun foregroundFor(background: Color): Color =
    foregroundForLuminance(background.luminance())
