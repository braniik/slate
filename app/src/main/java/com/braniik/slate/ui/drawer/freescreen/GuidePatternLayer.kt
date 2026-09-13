package com.braniik.slate.ui.drawer.freescreen

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BorderHorizontal
import androidx.compose.material.icons.filled.BorderVertical
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.braniik.slate.data.GuideLine
import com.braniik.slate.data.GuideOrientation
import com.braniik.slate.data.GuideRegion
import com.braniik.slate.data.LocalWallpaperTextColor
import com.braniik.slate.data.guideRegions
import com.braniik.slate.data.halvingGuide
import com.braniik.slate.data.plusUnique
import com.braniik.slate.ui.theme.SlateOnBackground
import com.braniik.slate.ui.theme.SlateSurface

// Halves smaller than this are not worth aiming an icon at, so we stop offering them.
private const val MIN_SIDE_DP = 56f

private val ACTION_SIZE = 48.dp

@Composable
internal fun GuidePatternLayer(
    guideLines: List<GuideLine>,
    containerSize: IntSize,
    onGuidesChanged: (List<GuideLine>) -> Unit
) {
    val density = LocalDensity.current
    val foreground = LocalWallpaperTextColor.current

    var localGuides by remember(guideLines) { mutableStateOf(guideLines) }
    var selected by remember(guideLines) { mutableStateOf<GuideRegion?>(null) }

    val widthDp = with(density) { containerSize.width.toDp().value }
    val heightDp = with(density) { containerSize.height.toDp().value }
    if (widthDp <= 0f || heightDp <= 0f) return

    val regions = remember(localGuides, widthDp, heightDp) {
        guideRegions(localGuides, widthDp, heightDp)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                drawGuideLines(localGuides, foreground.copy(alpha = 0.3f))

                val region = selected ?: return@drawBehind
                val topLeft = Offset(region.left.dp.toPx(), region.top.dp.toPx())
                val area = Size(region.width.dp.toPx(), region.height.dp.toPx())
                drawRect(foreground.copy(alpha = 0.10f), topLeft, area)
                drawRect(
                    foreground.copy(alpha = 0.55f),
                    topLeft,
                    area,
                    style = Stroke(GUIDE_LINE_WIDTH.toPx())
                )
            }
            .pointerInput(regions) {
                detectTapGestures { offset ->
                    val x = with(density) { offset.x.toDp().value }
                    val y = with(density) { offset.y.toDp().value }
                    val hit = regions.firstOrNull { it.contains(x, y) }
                    selected = if (hit == null || hit == selected) null else hit
                }
            }
    )

    selected?.let { region ->
        SplitActions(
            region = region,
            widthDp = widthDp,
            heightDp = heightDp,
            onSplit = { orientation ->
                region.halvingGuide(orientation, MIN_SIDE_DP)?.let { guide ->
                    localGuides = localGuides.plusUnique(guide)
                    onGuidesChanged(localGuides)
                    selected = null
                }
            }
        )
    }
}

@Composable
private fun SplitActions(
    region: GuideRegion,
    widthDp: Float,
    heightDp: Float,
    onSplit: (GuideOrientation) -> Unit
) {
    val canSplitVertically = region.halvingGuide(GuideOrientation.VERTICAL, MIN_SIDE_DP) != null
    val canSplitHorizontally = region.halvingGuide(GuideOrientation.HORIZONTAL, MIN_SIDE_DP) != null
    if (!canSplitVertically && !canSplitHorizontally) return

    val buttons = (if (canSplitVertically) 1 else 0) + (if (canSplitHorizontally) 1 else 0)
    val pillWidth = ACTION_SIZE.value * buttons
    val pillHeight = ACTION_SIZE.value

    val x = (region.midX() - pillWidth / 2f).coerceIn(0f, (widthDp - pillWidth).coerceAtLeast(0f))
    val y = (region.midY() - pillHeight / 2f).coerceIn(0f, (heightDp - pillHeight).coerceAtLeast(0f))

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .offset { IntOffset(x.dp.roundToPx(), y.dp.roundToPx()) }
            .clip(RoundedCornerShape(12.dp))
            .background(SlateSurface)
    ) {
        if (canSplitVertically) {
            SplitButton(Icons.Filled.BorderVertical, "split left and right") {
                onSplit(GuideOrientation.VERTICAL)
            }
        }
        if (canSplitHorizontally) {
            SplitButton(Icons.Filled.BorderHorizontal, "split top and bottom") {
                onSplit(GuideOrientation.HORIZONTAL)
            }
        }
    }
}

@Composable
private fun SplitButton(icon: ImageVector, label: String, onClick: () -> Unit) {
    IconButton(onClick = onClick, modifier = Modifier.size(ACTION_SIZE)) {
        Icon(icon, contentDescription = label, tint = SlateOnBackground, modifier = Modifier.size(20.dp))
    }
}