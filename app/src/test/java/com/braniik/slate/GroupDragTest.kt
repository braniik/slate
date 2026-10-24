package com.braniik.slate

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import com.braniik.slate.data.HomeScreenApp
import com.braniik.slate.ui.drawer.freescreen.GroupDrag
import com.braniik.slate.ui.drawer.freescreen.groupDeltaBounds
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class GroupDragTest {
    private val screen = Size(400f, 800f)

    private fun app(pkg: String, x: Float, y: Float) =
        HomeScreenApp(packageName = pkg, xPos = x, yPos = y)

    private fun measuredGroup(vararg apps: HomeScreenApp) = GroupDrag().apply {
        apps.forEach { measured("${it.packageName}:0", Size(40f, 40f)) }
    }

    @Test
    fun theGroupStopsWhereItsOutermostIconsHitTheEdges() {
        val boxes = listOf(Rect(10f, 10f, 50f, 50f), Rect(100f, 200f, 140f, 240f))
        assertEquals(Rect(-10f, -10f, 260f, 560f), groupDeltaBounds(boxes, 400f, 800f))
    }

    @Test
    fun anIconAlreadyPastAnEdgeOnlyStopsTheGroupGoingFurtherOut() {
        val boxes = listOf(Rect(-5f, 10f, 35f, 50f), Rect(380f, 10f, 420f, 50f))
        val bounds = groupDeltaBounds(boxes, 400f, 800f)
        assertEquals(0f, bounds.left)
        assertEquals(0f, bounds.right)
    }

    @Test
    fun dragAnUnselectedIconAndItGoesAlone() {
        val a = app("a", 0f, 0f)
        val b = app("b", 100f, 0f)
        val group = measuredGroup(a, b)
        assertNull(group.begin(a, setOf("b:0"), listOf(a, b), screen))
        assertNull(group.targets())
        assertNull(group.positionOf(b))
    }

    @Test
    fun aSelectionOfOneIsStillASoloDrag() {
        val a = app("a", 0f, 0f)
        assertNull(GroupDrag().begin(a, setOf("a:0"), listOf(a), screen))
    }

    @Test
    fun theLeadMayRoamOnlyAsFarAsTheWholeGroupFits() {
        val a = app("a", 50f, 50f)
        val b = app("b", 300f, 600f)
        val group = measuredGroup(a, b)
        val area = group.begin(a, setOf("a:0", "b:0"), listOf(a, b), screen)
        assertEquals(Rect(0f, 0f, 110f, 210f), area)
    }

    @Test
    fun followersCopyTheLeadsDisplacement() {
        val a = app("a", 50f, 50f)
        val b = app("b", 200f, 300f)
        val group = measuredGroup(a, b)
        group.begin(a, setOf("a:0", "b:0"), listOf(a, b), screen)
        group.follow(Offset(10f, -20f))
        assertEquals(Offset(210f, 280f), group.positionOf(b))
        assertEquals(
            mapOf("a:0" to Offset(60f, 30f), "b:0" to Offset(210f, 280f)),
            group.targets()
        )
    }

    @Test
    fun onceTheSaveLandsTheStoreIsTheTruth() {
        val a = app("a", 50f, 50f)
        val b = app("b", 200f, 300f)
        val group = measuredGroup(a, b)
        group.begin(a, setOf("a:0", "b:0"), listOf(a, b), screen)
        group.follow(Offset(10f, -20f))
        assertEquals(Offset(210f, 280f), group.positionOf(b.copy(xPos = 210f, yPos = 280f)))
    }

    @Test
    fun aSoloDragAfterAGroupDragForgetsTheGroup() {
        val a = app("a", 50f, 50f)
        val b = app("b", 200f, 300f)
        val c = app("c", 300f, 100f)
        val group = measuredGroup(a, b, c)
        group.begin(a, setOf("a:0", "b:0"), listOf(a, b, c), screen)
        group.follow(Offset(10f, 10f))
        group.begin(c, setOf("a:0", "b:0"), listOf(a, b, c), screen)
        assertNull(group.positionOf(b))
        assertNull(group.targets())
    }
}