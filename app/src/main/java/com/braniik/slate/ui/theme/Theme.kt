package com.braniik.slate.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val SlateColorScheme = darkColorScheme(
    background = SlateBackground,
    onBackground = SlateOnBackground,
    surface = SlateSurface,
    onSurface = SlateOnSurface,
    primary = SlateOnBackground,
    onPrimary = SlateBackground
)

@Composable
fun SlateTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = SlateColorScheme,
        typography = Typography,
        content = content
    )
}