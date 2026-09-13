package com.braniik.slate.ui.drawer.freescreen

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import com.braniik.slate.data.GuideLine
import com.braniik.slate.data.GuideOrientation

internal val GUIDE_LINE_WIDTH = 1.dp

internal fun DrawScope.drawGuideLines(guides: List<GuideLine>, color: Color) {
    val stroke = GUIDE_LINE_WIDTH.toPx()
    guides.forEach { guide ->
        val at = guide.positionDp.dp.toPx()
        when (guide.orientation) {
            GuideOrientation.VERTICAL ->
                drawLine(color, Offset(at, 0f), Offset(at, size.height), stroke)

            GuideOrientation.HORIZONTAL ->
                drawLine(color, Offset(0f, at), Offset(size.width, at), stroke)
        }
    }
}