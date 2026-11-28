package com.braniik.slate

import com.braniik.slate.data.HomeScreenApp
import com.braniik.slate.ui.drawer.freescreen.distribute
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class DistributeTest {
    private fun app(pkg: String, x: Float, y: Float): HomeScreenApp =
        HomeScreenApp(packageName = pkg, xPos = x, yPos = y)

    @Test
    fun invalidAxis() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            val apps = listOf(
                app("one", 1f, 1f),
                app("two", 2f, 2f),
                app("three", 3f, 3f)
            )
            distribute("junk", apps)
        }
        assertEquals("Unknown axis: junk", exception.message)
    }

    @Test
    fun axisOfNone() {
        val apps = listOf(
            app("one", 1f, 1f),
            app("two", 2f, 2f),
            app("three", 3f, 3f)
        )
        assertEquals(apps, distribute("none", apps))
    }

    @Test
    fun lessThanThreeApps() {
        val apps = listOf(
            app("one", 1f, 1f),
            app("two", 2f, 2f)
        )
        assertEquals(apps, distribute("horizontally", apps))
    }

    @Test
    fun xAxisThreeApps() {
        val apps = listOf(
            app("one", 1f, 5f),
            app("two", 2f, 6f),
            app("three", 10f, 3f)
        )
        assertEquals(
            listOf(
                app("one", 1f, 5f),
                app("two", 5.5f, 6f),
                app("three", 10f, 3f)
            ),
            distribute("horizontally", apps)
        )
    }

    @Test
    fun yAxisFourApps() {
        val apps = listOf(
            app("one", 1f, 5f),
            app("two", 2f, 6f),
            app("three", 3f, 7f),
            app("four", 4f, 14f),
        )
        assertEquals(
            listOf(
                app("one", 1f, 5f),
                app("two", 2f, 8f),
                app("three", 3f, 11f),
                app("four", 4f, 14f),
            ),
            distribute("vertically", apps)
        )
    }

    @Test
    fun appsOutOfOrder() {
        val apps = listOf(
            app("one", 1f, 5f),
            app("three", 10f, 3f),
            app("two", 2f, 6f),
        )
        assertEquals(
            listOf(
                app("one", 1f, 5f),
                app("two", 5.5f, 6f),
                app("three", 10f, 3f)
            ),
            distribute("horizontally", apps)
        )
    }
}