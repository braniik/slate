package com.braniik.slate.ui.drawer.common

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChangeIgnoreConsumed
import androidx.compose.ui.unit.dp
import kotlin.math.abs

private val SWIPE_DISTANCE = 48.dp

internal fun isSwipeUp(dx: Float, dy: Float, distancePx: Float): Boolean =
    -dy >= distancePx && -dy > 2 * abs(dx)

@Composable
fun Modifier.swipeUp(enabled: Boolean, onSwipeUp: () -> Unit): Modifier {
    val currentEnabled by rememberUpdatedState(enabled)
    val currentOnSwipeUp by rememberUpdatedState(onSwipeUp)

    return pointerInput(Unit) {
        val distancePx = SWIPE_DISTANCE.toPx()
        awaitEachGesture {
            val down = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
            if (!currentEnabled) return@awaitEachGesture

            var travel = Offset.Zero
            while (true) {
                val event = awaitPointerEvent(PointerEventPass.Initial)
                if (event.changes.size > 1) return@awaitEachGesture
                val change = event.changes.first()
                if (change.id != down.id || !change.pressed) return@awaitEachGesture

                travel += change.positionChangeIgnoreConsumed()
                if (isSwipeUp(travel.x, travel.y, distancePx)) {
                    change.consume()
                    currentOnSwipeUp()
                    do {
                        val rest = awaitPointerEvent(PointerEventPass.Initial)
                        rest.changes.forEach { it.consume() }
                    } while (rest.changes.any { it.pressed })
                    return@awaitEachGesture
                }
            }
        }
    }
}