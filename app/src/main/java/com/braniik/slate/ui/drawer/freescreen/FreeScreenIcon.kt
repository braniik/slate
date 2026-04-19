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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import com.braniik.slate.data.HomeScreenApp
import com.braniik.slate.ui.drawer.AppInfo
import com.braniik.slate.ui.drawer.HomeMode
import com.braniik.slate.ui.theme.SlateDanger
import com.braniik.slate.ui.theme.SlateOnBackground

@Composable
internal fun FreescreenIcon(
    homeApp: HomeScreenApp,
    info: AppInfo,
    containerSize: IntSize,
    mode: HomeMode,
    onTap: () -> Unit,
    onPositionChanged: (Float, Float) -> Unit
) {
    val density = LocalDensity.current

    var localX by remember(homeApp.packageName) { mutableFloatStateOf(homeApp.xPos) }
    var localY by remember(homeApp.packageName) { mutableFloatStateOf(homeApp.yPos) }

    LaunchedEffect(homeApp.xPos, homeApp.yPos) {
        localX = homeApp.xPos
        localY = homeApp.yPos
    }

    val canDrag = mode == HomeMode.NORMAL || mode == HomeMode.EDITING
    val containerWidthDp = with(density) { containerSize.width.toDp().value }
    val containerHeightDp = with(density) { containerSize.height.toDp().value }
    val iconFootprintDp = homeApp.iconSizeDp + 32f

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .offset { IntOffset(localX.dp.roundToPx(), localY.dp.roundToPx()) }
            .then(
                if (mode == HomeMode.DELETING) {
                    Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, SlateDanger, RoundedCornerShape(8.dp))
                } else Modifier
            )
            .pointerInput(homeApp.packageName) {
                detectTapGestures { onTap() }
            }
            .then(
                if (canDrag) {
                    Modifier.pointerInput(homeApp.packageName) {
                        detectDragGestures(
                            onDragEnd = { onPositionChanged(localX, localY) },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                val dxDp = with(density) { dragAmount.x.toDp().value }
                                val dyDp = with(density) { dragAmount.y.toDp().value }
                                val maxX = (containerWidthDp - iconFootprintDp).coerceAtLeast(0f)
                                val maxY = (containerHeightDp - iconFootprintDp).coerceAtLeast(0f)
                                localX = (localX + dxDp).coerceIn(0f, maxX)
                                localY = (localY + dyDp).coerceIn(0f, maxY)
                            }
                        )
                    }
                } else Modifier
            )
            .padding(8.dp)
    ) {
        val iconSize = homeApp.iconSizeDp.dp
        Image(
            bitmap = info.icon.toBitmap(iconSize.value.toInt(), iconSize.value.toInt()).asImageBitmap(),
            contentDescription = info.label,
            modifier = Modifier.size(iconSize)
        )
        if (homeApp.showLabel) {
            Text(
                text = info.label,
                fontSize = 10.sp,
                color = SlateOnBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}