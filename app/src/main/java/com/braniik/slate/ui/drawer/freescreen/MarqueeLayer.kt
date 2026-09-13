package com.braniik.slate.ui.drawer.freescreen

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.braniik.slate.data.HomeScreenApp
import com.braniik.slate.data.LocalWallpaperTextColor
import com.braniik.slate.data.key
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

internal fun iconKeysWithin(
    apps: List<HomeScreenApp>,
    leftDp: Float,
    topDp: Float,
    rightDp: Float,
    bottomDp: Float
): Set<String> {
    val hits = HashSet<String>()
    apps.forEach { app ->
        val left = app.xPos + ICON_PADDING_DP
        val top = app.yPos + ICON_PADDING_DP
        val right = left + app.iconSizeDp
        val bottom = top + app.iconSizeDp
        if (left < rightDp && right > leftDp && top < bottomDp && bottom > topDp) {
            hits += app.key
        }
    }
    return hits
}

internal class MarqueeState {
    var origin by mutableStateOf<Offset?>(null)
    var cursor by mutableStateOf(Offset.Zero)
}

@Composable
internal fun rememberMarqueeState(): MarqueeState = remember { MarqueeState() }

@Composable
internal fun Modifier.marqueeSelection(
    state: MarqueeState,
    apps: List<HomeScreenApp>,
    enabled: Boolean,
    onSelectionChanged: (Set<String>) -> Unit
): Modifier {
    val density = LocalDensity.current
    val foreground = LocalWallpaperTextColor.current
    val currentApps by rememberUpdatedState(apps)
    val currentOnSelectionChanged by rememberUpdatedState(onSelectionChanged)

    return this
        .drawBehind {
            val start = state.origin ?: return@drawBehind
            val cursor = state.cursor
            val topLeft = Offset(min(start.x, cursor.x), min(start.y, cursor.y))
            val area = Size(abs(cursor.x - start.x), abs(cursor.y - start.y))
            drawRect(foreground.copy(alpha = 0.08f), topLeft, area)
            drawRect(foreground.copy(alpha = 0.50f), topLeft, area, style = Stroke(1.dp.toPx()))
        }
        .pointerInput(enabled) {
            if (!enabled) {
                state.origin = null
                return@pointerInput
            }
            detectDragGestures(
                onDragStart = { start ->
                    state.origin = start
                    state.cursor = start
                },
                onDrag = { change, amount ->
                    change.consume()
                    state.cursor += amount
                    currentOnSelectionChanged(
                        selectionFor(state.origin, state.cursor, currentApps, density)
                    )
                },
                onDragEnd = { state.origin = null },
                onDragCancel = { state.origin = null }
            )
        }
}

private fun selectionFor(
    origin: Offset?,
    cursor: Offset,
    apps: List<HomeScreenApp>,
    density: Density
): Set<String> {
    val start = origin ?: return emptySet()
    with(density) {
        return iconKeysWithin(
            apps,
            min(start.x, cursor.x).toDp().value,
            min(start.y, cursor.y).toDp().value,
            max(start.x, cursor.x).toDp().value,
            max(start.y, cursor.y).toDp().value
        )
    }
}