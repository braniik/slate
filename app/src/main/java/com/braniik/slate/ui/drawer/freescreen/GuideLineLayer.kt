package com.braniik.slate.ui.drawer.freescreen

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.systemGestureExclusion
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.braniik.slate.data.GuideLine
import com.braniik.slate.data.GuideOrientation
import com.braniik.slate.data.LocalWallpaperTextColor
import kotlin.math.abs

private val EDGE_ZONE = 32.dp
private val GUIDE_HIT = 20.dp
private val DELETE_THRESHOLD = 30.dp
private val LINE_WIDTH = 1.dp

@Composable
internal fun GuideLineLayer(
    guideLines: List<GuideLine>,
    containerSize: IntSize,
    onGuidesChanged: (List<GuideLine>) -> Unit
) {
    val density = LocalDensity.current
    val lineColor = LocalWallpaperTextColor.current.copy(alpha = 0.3f)

    var localGuides by remember(guideLines) { mutableStateOf(guideLines) }

    val edgePx = with(density) { EDGE_ZONE.toPx() }
    val guideHitPx = with(density) { GUIDE_HIT.toPx() }
    val deletePx = with(density) { DELETE_THRESHOLD.toPx() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                val strokePx = LINE_WIDTH.toPx()
                localGuides.forEach { guide ->
                    val posPx = with(density) { guide.positionDp.dp.toPx() }
                    when (guide.orientation) {
                        GuideOrientation.VERTICAL ->
                            drawLine(lineColor, Offset(posPx, 0f), Offset(posPx, size.height), strokePx)
                        GuideOrientation.HORIZONTAL ->
                            drawLine(lineColor, Offset(0f, posPx), Offset(size.width, posPx), strokePx)
                    }
                }
            }
            .pointerInput(guideLines) {
                awaitEachGesture {
                    val down: PointerInputChange = awaitFirstDown(requireUnconsumed = false)
                    val pos: Offset = down.position

                    val hitGuide = localGuides.firstOrNull { guide ->
                        val guidePx = with(density) { guide.positionDp.dp.toPx() }
                        when (guide.orientation) {
                            GuideOrientation.VERTICAL -> abs(pos.x - guidePx) < guideHitPx
                            GuideOrientation.HORIZONTAL -> abs(pos.y - guidePx) < guideHitPx
                        }
                    }

                    val edgeCreate: GuideOrientation? = when {
                        hitGuide != null -> null
                        pos.x < edgePx -> GuideOrientation.VERTICAL
                        pos.x > size.width - edgePx -> GuideOrientation.VERTICAL
                        pos.y < edgePx -> GuideOrientation.HORIZONTAL
                        pos.y > size.height - edgePx -> GuideOrientation.HORIZONTAL
                        else -> null
                    }

                    if (hitGuide == null && edgeCreate == null) return@awaitEachGesture

                    down.consume()

                    if (hitGuide != null) {
                        var currentPx = when (hitGuide.orientation) {
                            GuideOrientation.VERTICAL -> pos.x
                            GuideOrientation.HORIZONTAL -> pos.y
                        }

                        while (true) {
                            val event = awaitPointerEvent()
                            val change: PointerInputChange = event.changes.firstOrNull() ?: break

                            if (!change.pressed) {
                                val shouldDelete = when (hitGuide.orientation) {
                                    GuideOrientation.VERTICAL ->
                                        currentPx < deletePx || currentPx > size.width - deletePx
                                    GuideOrientation.HORIZONTAL ->
                                        currentPx > size.height - deletePx || currentPx < deletePx
                                }
                                localGuides = if (shouldDelete) {
                                    localGuides.filter { it.id != hitGuide.id }
                                } else {
                                    localGuides.map {
                                        if (it.id == hitGuide.id)
                                            it.copy(positionDp = with(density) { currentPx.toDp().value })
                                        else it
                                    }
                                }
                                onGuidesChanged(localGuides)
                                break
                            }

                            change.consume()
                            currentPx = when (hitGuide.orientation) {
                                GuideOrientation.VERTICAL -> change.position.x
                                GuideOrientation.HORIZONTAL -> change.position.y
                            }

                            localGuides = localGuides.map {
                                if (it.id == hitGuide.id)
                                    it.copy(positionDp = with(density) { currentPx.toDp().value })
                                else it
                            }
                        }
                    } else if (edgeCreate != null) {
                        val newId = java.util.UUID.randomUUID().toString()
                        var currentPx = when (edgeCreate) {
                            GuideOrientation.VERTICAL -> pos.x
                            GuideOrientation.HORIZONTAL -> pos.y
                        }
                        val initialDp = with(density) { currentPx.toDp().value }
                        val newGuide = GuideLine(newId, edgeCreate, initialDp)
                        localGuides = localGuides + newGuide

                        while (true) {
                            val event = awaitPointerEvent()
                            val change: PointerInputChange = event.changes.firstOrNull() ?: break

                            if (!change.pressed) {
                                val shouldKeep = when (edgeCreate) {
                                    GuideOrientation.VERTICAL ->
                                        currentPx > deletePx && currentPx < size.width - deletePx
                                    GuideOrientation.HORIZONTAL ->
                                        currentPx > deletePx && currentPx < size.height - deletePx
                                }
                                if (!shouldKeep) {
                                    localGuides = localGuides.filter { it.id != newId }
                                } else {
                                    localGuides = localGuides.map {
                                        if (it.id == newId)
                                            it.copy(positionDp = with(density) { currentPx.toDp().value })
                                        else it
                                    }
                                }
                                onGuidesChanged(localGuides)
                                break
                            }

                            change.consume()
                            currentPx = when (edgeCreate) {
                                GuideOrientation.VERTICAL -> change.position.x
                                GuideOrientation.HORIZONTAL -> change.position.y
                            }

                            localGuides = localGuides.map {
                                if (it.id == newId)
                                    it.copy(positionDp = with(density) { currentPx.toDp().value })
                                else it
                            }
                        }
                    }
                }
            }
    ) {
        Box(
            Modifier
                .align(Alignment.CenterStart)
                .width(EDGE_ZONE)
                .height(200.dp)
                .systemGestureExclusion()
        )
        Box(
            Modifier
                .align(Alignment.CenterEnd)
                .width(EDGE_ZONE)
                .height(200.dp)
                .systemGestureExclusion()
        )
    }
}