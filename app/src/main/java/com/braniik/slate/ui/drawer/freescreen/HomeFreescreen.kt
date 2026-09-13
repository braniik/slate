package com.braniik.slate.ui.drawer.freescreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import com.braniik.slate.data.GuideLine
import com.braniik.slate.data.key
import com.braniik.slate.data.HomeScreenApp
import com.braniik.slate.ui.drawer.AppInfo
import com.braniik.slate.ui.drawer.HomeMode

@Composable
fun HomeFreescreen(
    homeApps: List<HomeScreenApp>,
    allApps: List<AppInfo>,
    mode: HomeMode,
    guideLines: List<GuideLine>,
    patternMode: Boolean = false,
    selection: Set<String> = emptySet(),
    onSelectionChanged: (Set<String>) -> Unit = {},
    onTap: (HomeScreenApp) -> Unit,
    onLongPress: (HomeScreenApp) -> Unit = {},
    onPositionChanged: (HomeScreenApp, Float, Float) -> Unit,
    onGuidesChanged: (List<GuideLine>) -> Unit
) {
    var containerSize by remember { mutableStateOf(IntSize.Zero) }
    val marquee = rememberMarqueeState()

    val editing = mode == HomeMode.EDITING
    val iconMode = if (patternMode) HomeMode.NORMAL else mode

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { containerSize = it }
            .marqueeSelection(
                state = marquee,
                apps = homeApps,
                enabled = editing && !patternMode,
                onSelectionChanged = onSelectionChanged
            )
    ) {
        if (editing && !patternMode) {
            GuideLineLayer(
                guideLines = guideLines,
                containerSize = containerSize,
                onGuidesChanged = onGuidesChanged
            )
        }

        homeApps.forEach { homeApp ->
            val info = allApps.find { it.key == homeApp.key } ?: return@forEach
            key(homeApp.key) {
                FreescreenIcon(
                    homeApp = homeApp,
                    info = info,
                    containerSize = containerSize,
                    mode = iconMode,
                    guideLines = guideLines,
                    selected = homeApp.key in selection,
                    onTap = { onTap(homeApp) },
                    onLongPress = { onLongPress(homeApp) },
                    onPositionChanged = { x, y -> onPositionChanged(homeApp, x, y) }
                )
            }
        }

        if (editing && patternMode) {
            GuidePatternLayer(
                guideLines = guideLines,
                containerSize = containerSize,
                onGuidesChanged = onGuidesChanged
            )
        }
    }
}