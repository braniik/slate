package com.braniik.slate.ui.drawer.freescreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.braniik.slate.data.GuideLine
import com.braniik.slate.data.GuideOrientation
import com.braniik.slate.data.HomeScreenApp
import com.braniik.slate.data.LocalWallpaperTextColor
import com.braniik.slate.data.key
import com.braniik.slate.ui.drawer.AppInfo
import com.braniik.slate.ui.drawer.HomeMode
import com.braniik.slate.ui.drawer.common.iconShapeFor
import com.braniik.slate.ui.drawer.rememberAppIcon
import com.braniik.slate.ui.theme.SlateDanger
import kotlin.math.abs

private const val PADDING_DP = 8f

@Composable
internal fun FreescreenIcon(
    homeApp: HomeScreenApp,
    info: AppInfo,
    containerSize: IntSize,
    mode: HomeMode,
    guideLines: List<GuideLine> = emptyList(),
    onTap: () -> Unit,
    onLongPress: () -> Unit = {},
    onPositionChanged: (Float, Float) -> Unit
) {
    val density = LocalDensity.current

    var localX by remember(homeApp.key) { mutableFloatStateOf(homeApp.xPos) }
    var localY by remember(homeApp.key) { mutableFloatStateOf(homeApp.yPos) }
    LaunchedEffect(homeApp.xPos, homeApp.yPos) {
        localX = homeApp.xPos
        localY = homeApp.yPos
    }

    val currentGuides by rememberUpdatedState(guideLines)
    val currentContainer by rememberUpdatedState(containerSize)
    val currentIconSizeDp by rememberUpdatedState(homeApp.iconSizeDp)
    val currentOnPositionChanged by rememberUpdatedState(onPositionChanged)

    val iconFootprintDp = homeApp.iconSizeDp + 2 * PADDING_DP
    var measuredWidthDp by remember { mutableFloatStateOf(iconFootprintDp) }
    var measuredHeightDp by remember { mutableFloatStateOf(iconFootprintDp) }

    val icon = rememberAppIcon(info, homeApp.iconSizeDp)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .onSizeChanged {
                measuredWidthDp = with(density) { it.width.toDp().value }
                measuredHeightDp = with(density) { it.height.toDp().value }
            }
            .offset { IntOffset(localX.dp.roundToPx(), localY.dp.roundToPx()) }
            .then(
                if (mode == HomeMode.DELETING) {
                    Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, SlateDanger, RoundedCornerShape(8.dp))
                } else Modifier
            )
            .pointerInput(homeApp.key) {
                detectTapGestures(
                    onLongPress = { onLongPress() },
                    onTap = { onTap() }
                )
            }
            .then(
                if (mode == HomeMode.EDITING) {
                    Modifier.pointerInput(homeApp.key) {
                        val snapX = AxisSnap()
                        val snapY = AxisSnap()
                        var dragStartX = 0f
                        var dragStartY = 0f
                        detectDragGestures(
                            onDragStart = {
                                snapX.reset()
                                snapY.reset()
                                dragStartX = localX
                                dragStartY = localY
                            },
                            onDragEnd = {
                                if (abs(localX - dragStartX) > 0.5f || abs(localY - dragStartY) > 0.5f) {
                                    currentOnPositionChanged(localX, localY)
                                }
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                val dxDp = with(density) { dragAmount.x.toDp().value }
                                val dyDp = with(density) { dragAmount.y.toDp().value }
                                val containerWidthDp = with(density) { currentContainer.width.toDp().value }
                                val containerHeightDp = with(density) { currentContainer.height.toDp().value }
                                val maxX = (containerWidthDp - measuredWidthDp).coerceAtLeast(0f)
                                val maxY = (containerHeightDp - measuredHeightDp).coerceAtLeast(0f)
                                val centerOffsetXDp = measuredWidthDp / 2f
                                val centerOffsetYDp = PADDING_DP + currentIconSizeDp / 2f

                                localX = snapX.move(
                                    raw = localX + dxDp, delta = dxDp, centerOffset = centerOffsetXDp,
                                    max = maxX, guides = currentGuides, orientation = GuideOrientation.VERTICAL
                                )
                                localY = snapY.move(
                                    raw = localY + dyDp, delta = dyDp, centerOffset = centerOffsetYDp,
                                    max = maxY, guides = currentGuides, orientation = GuideOrientation.HORIZONTAL
                                )
                            }
                        )
                    }
                } else Modifier
            )
            .padding(PADDING_DP.dp)
    ) {
        Image(
            bitmap = icon,
            contentDescription = info.label,
            modifier = Modifier
                .size(homeApp.iconSizeDp.dp)
                .rotate(homeApp.rotationDeg)
                .clip(iconShapeFor(homeApp.iconShape))
        )
        if (homeApp.showLabel) {
            Text(
                text = info.label,
                fontSize = 10.sp,
                color = LocalWallpaperTextColor.current,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

private const val SNAP_THRESHOLD_DP = 15f
private const val BREAK_THRESHOLD_DP = 25f

private class AxisSnap {
    private var snapped: GuideLine? = null
    private var perpAccum = 0f

    fun reset() {
        snapped = null
        perpAccum = 0f
    }

    fun move(
        raw: Float,
        delta: Float,
        centerOffset: Float,
        max: Float,
        guides: List<GuideLine>,
        orientation: GuideOrientation
    ): Float {
        val stuck = snapped
        if (stuck != null) {
            perpAccum += delta
            if (abs(perpAccum) > BREAK_THRESHOLD_DP) {
                val released = (stuck.positionDp - centerOffset + perpAccum).coerceIn(0f, max)
                reset()
                return released
            }
            return (stuck.positionDp - centerOffset).coerceIn(0f, max)
        }

        val pos = raw.coerceIn(0f, max)
        val center = pos + centerOffset
        val near = guides
            .filter { it.orientation == orientation }
            .minByOrNull { abs(center - it.positionDp) }
        if (near != null && abs(center - near.positionDp) < SNAP_THRESHOLD_DP) {
            snapped = near
            perpAccum = 0f
            return (near.positionDp - centerOffset).coerceIn(0f, max)
        }
        return pos
    }
}