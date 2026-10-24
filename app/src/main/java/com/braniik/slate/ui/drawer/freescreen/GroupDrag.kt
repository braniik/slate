package com.braniik.slate.ui.drawer.freescreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import com.braniik.slate.data.HomeScreenApp
import com.braniik.slate.data.key

internal class GroupDrag {
    private var origins by mutableStateOf<Map<String, Offset>>(emptyMap())
    private var delta by mutableStateOf(Offset.Zero)
    private val sizes = HashMap<String, Size>()

    fun measured(key: String, size: Size) {
        sizes[key] = size
    }

    fun begin(
        lead: HomeScreenApp,
        selection: Set<String>,
        apps: List<HomeScreenApp>,
        container: Size
    ): Rect? {
        delta = Offset.Zero
        val members =
            if (lead.key in selection) apps.filter { it.key in selection } else emptyList()
        if (members.size < 2) {
            origins = emptyMap()
            return null
        }
        origins = members.associate { it.key to Offset(it.xPos, it.yPos) }
        return groupDeltaBounds(members.map(::boxOf), container.width, container.height)
            .translate(Offset(lead.xPos, lead.yPos))
    }

    fun follow(displacement: Offset) {
        delta = displacement
    }

    fun targets(): Map<String, Offset>? =
        origins.takeIf { it.isNotEmpty() }?.mapValues { (_, origin) -> origin + delta }

    fun positionOf(app: HomeScreenApp): Offset? {
        val origin = origins[app.key] ?: return null
        val stored = Offset(app.xPos, app.yPos)
        return if (stored == origin) origin + delta else stored
    }

    private fun boxOf(app: HomeScreenApp): Rect {
        val footprint = app.iconSizeDp + 2 * ICON_PADDING_DP
        return Rect(Offset(app.xPos, app.yPos), sizes[app.key] ?: Size(footprint, footprint))
    }
}

internal fun groupDeltaBounds(boxes: List<Rect>, width: Float, height: Float): Rect {
    if (boxes.isEmpty()) return Rect.Zero
    return Rect(
        left = (-boxes.minOf { it.left }).coerceAtMost(0f),
        top = (-boxes.minOf { it.top }).coerceAtMost(0f),
        right = (width - boxes.maxOf { it.right }).coerceAtLeast(0f),
        bottom = (height - boxes.maxOf { it.bottom }).coerceAtLeast(0f)
    )
}