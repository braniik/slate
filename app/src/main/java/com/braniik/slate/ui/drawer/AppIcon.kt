package com.braniik.slate.ui.drawer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

@Composable
fun rememberAppIcon(info: AppInfo, sizeDp: Int): ImageBitmap {
    val density = LocalDensity.current
    val sizePx = with(density) { sizeDp.dp.roundToPx() }
    return remember(info, sizePx) { info.bitmap(sizePx) }
}