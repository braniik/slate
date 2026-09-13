package com.braniik.slate

import com.braniik.slate.data.HomeScreenApp
import com.braniik.slate.ui.drawer.freescreen.iconKeysWithin
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MarqueeSelectionTest {
    private fun app(pkg: String, x: Float, y: Float, size: Int = 56) =
        HomeScreenApp(packageName = pkg, iconSizeDp = size, xPos = x, yPos = y)

    @Test
    fun aBoxOverAnIconSelectsIt() {
        val apps = listOf(app("a", 100f, 100f))
        assertEquals(setOf("a:0"), iconKeysWithin(apps, 90f, 90f, 200f, 200f))
    }

    @Test
    fun touchingAnIconIsEnough() {
        val apps = listOf(app("a", 100f, 100f))
        assertTrue(iconKeysWithin(apps, 0f, 0f, 120f, 120f).isNotEmpty())
    }

    @Test
    fun theGapAroundAnIconIsNotTheIcon() {
        val apps = listOf(app("a", 100f, 100f))
        assertTrue(iconKeysWithin(apps, 98f, 98f, 106f, 106f).isEmpty())
    }

    @Test
    fun aBoxCanCatchSeveralAtOnce() {
        val apps = listOf(app("a", 0f, 0f), app("b", 100f, 0f), app("c", 0f, 400f))
        assertEquals(setOf("a:0", "b:0"), iconKeysWithin(apps, 0f, 0f, 300f, 100f))
    }

    @Test
    fun anEmptyBoxSelectsNothing() {
        val apps = listOf(app("a", 0f, 0f))
        assertEquals(emptySet<String>(), iconKeysWithin(apps, 500f, 500f, 600f, 600f))
    }
}