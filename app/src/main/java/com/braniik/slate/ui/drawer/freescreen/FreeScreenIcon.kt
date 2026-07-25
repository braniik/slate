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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.braniik.slate.data.key
import com.braniik.slate.data.GuideOrientation
import com.braniik.slate.data.HomeScreenApp
import com.braniik.slate.data.LocalWallpaperTextColor
import com.braniik.slate.ui.drawer.AppInfo
import com.braniik.slate.ui.drawer.HomeMode
import com.braniik.slate.ui.drawer.common.iconShapeFor
import com.braniik.slate.ui.theme.SlateDanger
import kotlin.math.abs

private const val SNAP_THRESHOLD_DP = 15f
private const val BREAK_THRESHOLD_DP = 25f
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
    var localX by remember(homeApp.key, homeApp.xPos) { mutableFloatStateOf(homeApp.xPos) }
    var localY by remember(homeApp.key, homeApp.yPos) { mutableFloatStateOf(homeApp.yPos) }

    val canDrag = mode == HomeMode.NORMAL || mode == HomeMode.EDITING
    val containerWidthDp = with(density) { containerSize.width.toDp().value }
    val containerHeightDp = with(density) { containerSize.height.toDp().value }
    val iconFootprintDp = homeApp.iconSizeDp + 2 * PADDING_DP
    val iconHalfDp = homeApp.iconSizeDp / 2f

    val centerOffsetYDp = PADDING_DP + iconHalfDp
    var measuredWidthDp by remember { mutableFloatStateOf(iconFootprintDp) }
    var measuredHeightDp by remember { mutableFloatStateOf(iconFootprintDp) }

    var dragStartX by remember { mutableFloatStateOf(0f) }
    var dragStartY by remember { mutableFloatStateOf(0f) }
    var snappedVertical by remember { mutableStateOf<GuideLine?>(null) }
    var snappedHorizontal by remember { mutableStateOf<GuideLine?>(null) }
    var perpAccumX by remember { mutableFloatStateOf(0f) }
    var perpAccumY by remember { mutableFloatStateOf(0f) }

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
                if (canDrag) {
                    Modifier.pointerInput(homeApp.key, homeApp.iconSizeDp, guideLines) {
                        detectDragGestures(
                            onDragStart = {
                                snappedVertical = null
                                snappedHorizontal = null
                                perpAccumX = 0f
                                perpAccumY = 0f
                                dragStartX = localX
                                dragStartY = localY
                            },
                            onDragEnd = {
                                if (abs(localX - dragStartX) > 0.5f || abs(localY - dragStartY) > 0.5f) {
                                    onPositionChanged(localX, localY)
                                }
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                val dxDp = with(density) { dragAmount.x.toDp().value }
                                val dyDp = with(density) { dragAmount.y.toDp().value }
                                val maxX = (containerWidthDp - measuredWidthDp).coerceAtLeast(0f)
                                val maxY = (containerHeightDp - measuredHeightDp).coerceAtLeast(0f)
                                val centerOffsetXDp = measuredWidthDp / 2f

                                val rawNewX = localX + dxDp
                                val rawNewY = localY + dyDp
                                val sv = snappedVertical
                                var newX: Float
                                if (sv != null) {
                                    perpAccumX += dxDp
                                    if (abs(perpAccumX) > BREAK_THRESHOLD_DP) {
                                        newX = (sv.positionDp - centerOffsetXDp + perpAccumX)
                                            .coerceIn(0f, maxX)
                                        snappedVertical = null
                                        perpAccumX = 0f
                                    } else {
                                        newX = (sv.positionDp - centerOffsetXDp).coerceIn(0f, maxX)
                                    }
                                } else {
                                    newX = rawNewX.coerceIn(0f, maxX)
                                    val centerX = newX + centerOffsetXDp
                                    val nearV = guideLines
                                        .filter { it.orientation == GuideOrientation.VERTICAL }
                                        .minByOrNull { abs(centerX - it.positionDp) }
                                    if (nearV != null && abs(centerX - nearV.positionDp) < SNAP_THRESHOLD_DP) {
                                        snappedVertical = nearV
                                        perpAccumX = 0f
                                        newX = (nearV.positionDp - centerOffsetXDp).coerceIn(0f, maxX)
                                    }
                                }

                                val sh = snappedHorizontal
                                var newY: Float
                                if (sh != null) {
                                    perpAccumY += dyDp
                                    if (abs(perpAccumY) > BREAK_THRESHOLD_DP) {
                                        newY = (sh.positionDp - centerOffsetYDp + perpAccumY)
                                            .coerceIn(0f, maxY)
                                        snappedHorizontal = null
                                        perpAccumY = 0f
                                    } else {
                                        newY = (sh.positionDp - centerOffsetYDp).coerceIn(0f, maxY)
                                    }
                                } else {
                                    newY = rawNewY.coerceIn(0f, maxY)
                                    val centerY = newY + centerOffsetYDp
                                    val nearH = guideLines
                                        .filter { it.orientation == GuideOrientation.HORIZONTAL }
                                        .minByOrNull { abs(centerY - it.positionDp) }
                                    if (nearH != null && abs(centerY - nearH.positionDp) < SNAP_THRESHOLD_DP) {
                                        snappedHorizontal = nearH
                                        perpAccumY = 0f
                                        newY = (nearH.positionDp - centerOffsetYDp).coerceIn(0f, maxY)
                                    }
                                }

                                localX = newX
                                localY = newY
                            }
                        )
                    }
                } else Modifier
            )
            .padding(PADDING_DP.dp)
    ) {
        Image(
            bitmap = info.icon,
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