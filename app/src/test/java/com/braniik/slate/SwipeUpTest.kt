package com.braniik.slate

import com.braniik.slate.ui.drawer.common.isSwipeUp
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SwipeUpTest {
    private val distance = 100f

    @Test
    fun farEnoughStraightUpCounts() {
        assertTrue(isSwipeUp(dx = 0f, dy = -100f, distancePx = distance))
    }

    @Test
    fun tooShortDoesNotCount() {
        assertFalse(isSwipeUp(dx = 0f, dy = -99f, distancePx = distance))
    }

    @Test
    fun downwardNeverCounts() {
        assertFalse(isSwipeUp(dx = 0f, dy = 300f, distancePx = distance))
    }

    @Test
    fun aMostlySidewaysDragIsNotASwipeUp() {
        assertFalse(isSwipeUp(dx = 80f, dy = -120f, distancePx = distance))
        assertTrue(isSwipeUp(dx = -50f, dy = -120f, distancePx = distance))
    }
}