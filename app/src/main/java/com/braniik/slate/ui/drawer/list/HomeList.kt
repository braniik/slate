package com.braniik.slate.ui.drawer.list

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.braniik.slate.data.HomeScreenApp
import com.braniik.slate.data.key
import com.braniik.slate.data.LocalWallpaperTextColor
import com.braniik.slate.ui.drawer.AppInfo
import com.braniik.slate.ui.drawer.HomeMode
import com.braniik.slate.ui.drawer.common.iconShapeFor
import com.braniik.slate.ui.drawer.rememberAppIcon
import com.braniik.slate.ui.theme.SlateDanger

@Composable
fun HomeList(
    homeApps: List<HomeScreenApp>,
    allApps: List<AppInfo>,
    mode: HomeMode,
    horizontal: Boolean,
    onTap: (HomeScreenApp) -> Unit,
    onLongPress: (HomeScreenApp) -> Unit = {},
    onReorder: (List<HomeScreenApp>) -> Unit = {}
) {
    val canDrag = mode == HomeMode.EDITING

    val items = remember { mutableStateListOf<HomeScreenApp>() }
    LaunchedEffect(homeApps) {
        items.clear()
        items.addAll(homeApps)
    }

    var dragIndex by remember { mutableIntStateOf(-1) }
    var dragOffsetPx by remember { mutableFloatStateOf(0f) }
    val listState = rememberLazyListState()

    val trySwap: () -> Unit = {
        if (dragIndex >= 0) {
            val layout = listState.layoutInfo
            val dragged = layout.visibleItemsInfo.find { it.index == dragIndex }
            if (dragged != null) {
                val draggedCenter = dragged.offset + dragged.size / 2 + dragOffsetPx.toInt()
                for (target in layout.visibleItemsInfo) {
                    if (target.index == dragIndex) continue
                    val targetCenter = target.offset + target.size / 2
                    val shouldSwap =
                        (dragIndex < target.index && draggedCenter > targetCenter) ||
                                (dragIndex > target.index && draggedCenter < targetCenter)
                    if (shouldSwap) {
                        val prevOffset = dragged.offset
                        items.apply { add(target.index, removeAt(dragIndex)) }
                        dragOffsetPx += (prevOffset - target.offset).toFloat()
                        dragIndex = target.index
                        break
                    }
                }
            }
        }
    }

    val commitDrag: () -> Unit = {
        if (dragIndex >= 0) {
            onReorder(items.mapIndexed { i, app -> app.copy(order = i) })
        }
        dragIndex = -1
        dragOffsetPx = 0f
    }

    LaunchedEffect(canDrag) {
        if (!canDrag && dragIndex >= 0) {
            onReorder(items.mapIndexed { i, app -> app.copy(order = i) })
            dragIndex = -1
            dragOffsetPx = 0f
        }
    }

    if (horizontal) {
        LazyRow(
            state = listState,
            userScrollEnabled = !canDrag,
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize()
        ) {
            itemsIndexed(items, key = { _, app -> app.key }) { index, homeApp ->
                val info = allApps.find { it.key == homeApp.key }
                    ?: return@itemsIndexed
                val isDragged = index == dragIndex
                HorizontalListItem(
                    homeApp = homeApp,
                    info = info,
                    mode = mode,
                    dragOffsetPx = if (isDragged) dragOffsetPx else 0f,
                    onTap = onTap,
                    onLongPress = onLongPress,
                    onDragStart = { dragIndex = index; dragOffsetPx = 0f },
                    onDrag = { delta -> dragOffsetPx += delta; trySwap() },
                    onDragEnd = commitDrag,
                    modifier = Modifier
                        .zIndex(if (isDragged) 1f else 0f)
                        .then(if (canDrag && !isDragged) Modifier.animateItem() else Modifier)
                )
            }
        }
    } else {
        LazyColumn(
            state = listState,
            userScrollEnabled = !canDrag,
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            itemsIndexed(items, key = { _, app -> app.key }) { index, homeApp ->
                val info = allApps.find { it.key == homeApp.key }
                    ?: return@itemsIndexed
                val isDragged = index == dragIndex
                VerticalListItem(
                    homeApp = homeApp,
                    info = info,
                    mode = mode,
                    dragOffsetPx = if (isDragged) dragOffsetPx else 0f,
                    onTap = onTap,
                    onLongPress = onLongPress,
                    onDragStart = { dragIndex = index; dragOffsetPx = 0f },
                    onDrag = { delta -> dragOffsetPx += delta; trySwap() },
                    onDragEnd = commitDrag,
                    modifier = Modifier
                        .zIndex(if (isDragged) 1f else 0f)
                        .then(if (canDrag && !isDragged) Modifier.animateItem() else Modifier)
                )
            }
        }
    }
}

@Composable
private fun VerticalListItem(
    homeApp: HomeScreenApp,
    info: AppInfo,
    mode: HomeMode,
    dragOffsetPx: Float,
    onTap: (HomeScreenApp) -> Unit,
    onLongPress: (HomeScreenApp) -> Unit,
    onDragStart: () -> Unit,
    onDrag: (Float) -> Unit,
    onDragEnd: () -> Unit,
    modifier: Modifier = Modifier
) {
    val canDrag = mode == HomeMode.EDITING
    val currentOnDragStart by rememberUpdatedState(onDragStart)
    val currentOnDrag by rememberUpdatedState(onDrag)
    val currentOnDragEnd by rememberUpdatedState(onDragEnd)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .then(deleteBorder(mode))
            .offset { IntOffset(0, dragOffsetPx.toInt()) }
            .pointerInput(homeApp.key) {
                detectTapGestures(
                    onLongPress = { onLongPress(homeApp) },
                    onTap = { onTap(homeApp) }
                )
            }
            .then(
                if (canDrag) {
                    Modifier.pointerInput(homeApp.key) {
                        detectDragGestures(
                            onDragStart = { currentOnDragStart() },
                            onDrag = { change, amount ->
                                change.consume()
                                currentOnDrag(amount.y)
                            },
                            onDragEnd = { currentOnDragEnd() },
                            onDragCancel = { currentOnDragEnd() }
                        )
                    }
                } else Modifier
            )
            .padding(vertical = 10.dp, horizontal = 4.dp)
    ) {
        if (homeApp.showLabel) {
            val iconSize = homeApp.listIconSizeDp.dp
            Image(
                bitmap = rememberAppIcon(info, homeApp.listIconSizeDp),
                contentDescription = info.label,
                modifier = Modifier
                    .size(iconSize)
                    .rotate(homeApp.rotationDeg)
                    .clip(iconShapeFor(homeApp.iconShape))
            )
            Spacer(Modifier.width(16.dp))
        }

        Text(
            text = info.label,
            fontSize = homeApp.listTextSizeSp.sp,
            color = LocalWallpaperTextColor.current,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun HorizontalListItem(
    homeApp: HomeScreenApp,
    info: AppInfo,
    mode: HomeMode,
    dragOffsetPx: Float,
    onTap: (HomeScreenApp) -> Unit,
    onLongPress: (HomeScreenApp) -> Unit,
    onDragStart: () -> Unit,
    onDrag: (Float) -> Unit,
    onDragEnd: () -> Unit,
    modifier: Modifier = Modifier
) {
    val canDrag = mode == HomeMode.EDITING
    val currentOnDragStart by rememberUpdatedState(onDragStart)
    val currentOnDrag by rememberUpdatedState(onDrag)
    val currentOnDragEnd by rememberUpdatedState(onDragEnd)

    val iconSize = homeApp.listIconSizeDp.dp

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = modifier
            .then(deleteBorder(mode))
            .offset { IntOffset(dragOffsetPx.toInt(), 0) }
            .pointerInput(homeApp.key) {
                detectTapGestures(
                    onLongPress = { onLongPress(homeApp) },
                    onTap = { onTap(homeApp) }
                )
            }
            .then(
                if (canDrag) {
                    Modifier.pointerInput(homeApp.key) {
                        detectDragGestures(
                            onDragStart = { currentOnDragStart() },
                            onDrag = { change, amount ->
                                change.consume()
                                currentOnDrag(amount.x)
                            },
                            onDragEnd = { currentOnDragEnd() },
                            onDragCancel = { currentOnDragEnd() }
                        )
                    }
                } else Modifier
            )
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        if (homeApp.showLabel) {
            Image(
                bitmap = rememberAppIcon(info, homeApp.listIconSizeDp),
                contentDescription = info.label,
                modifier = Modifier
                    .size(iconSize)
                    .rotate(homeApp.rotationDeg)
                    .clip(iconShapeFor(homeApp.iconShape))
            )
        }

        Text(
            text = info.label,
            fontSize = homeApp.listTextSizeSp.sp,
            color = LocalWallpaperTextColor.current,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

private fun deleteBorder(mode: HomeMode): Modifier =
    if (mode == HomeMode.DELETING) {
        Modifier
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, SlateDanger, RoundedCornerShape(8.dp))
    } else Modifier