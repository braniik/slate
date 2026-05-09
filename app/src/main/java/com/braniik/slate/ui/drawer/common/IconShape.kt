package com.braniik.slate.ui.drawer.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.braniik.slate.ui.theme.SlateOnBackground
import com.braniik.slate.ui.theme.SlateSubtle
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

const val SHAPE_ROUND = "round"
const val SHAPE_SQUARE = "square"
const val SHAPE_SQUIRCLE = "squircle"
const val SHAPE_HEXAGON = "hexagon"
const val SHAPE_OCTAGON = "octagon"

val ALL_SHAPES = listOf(SHAPE_ROUND, SHAPE_SQUARE, SHAPE_SQUIRCLE, SHAPE_HEXAGON, SHAPE_OCTAGON)

fun iconShapeFor(name: String): Shape = when (name) {
    SHAPE_ROUND -> CircleShape
    SHAPE_SQUARE -> RectangleShape
    SHAPE_SQUIRCLE -> RoundedCornerShape(22)
    SHAPE_HEXAGON -> HexagonShape
    SHAPE_OCTAGON -> OctagonShape
    else -> CircleShape
}

private val HexagonShape = object : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val r = min(size.width, size.height) / 2f
        val cx = size.width / 2f
        val cy = size.height / 2f
        val path = Path()
        for (i in 0..5) {
            val angle = Math.toRadians((60.0 * i) - 90.0)
            val x = cx + r * cos(angle).toFloat()
            val y = cy + r * sin(angle).toFloat()
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()
        return Outline.Generic(path)
    }
}

private val OctagonShape = object : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val r = min(size.width, size.height) / 2f
        val cx = size.width / 2f
        val cy = size.height / 2f
        val path = Path()
        for (i in 0..7) {
            val angle = Math.toRadians((45.0 * i) - 22.5)
            val x = cx + r * cos(angle).toFloat()
            val y = cy + r * sin(angle).toFloat()
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()
        return Outline.Generic(path)
    }
}

@Composable
fun IconShapePicker(selected: String, onSelect: (String) -> Unit) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth()
    ) {
        ALL_SHAPES.forEach { shape ->
            val isSelected = shape == selected
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(iconShapeFor(shape))
                    .background(if (isSelected) SlateOnBackground else SlateSubtle)
                    .clickable { onSelect(shape) }
            )
        }
    }
}