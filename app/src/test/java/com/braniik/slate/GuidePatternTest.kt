package com.braniik.slate

import com.braniik.slate.data.GuideLine
import com.braniik.slate.data.GuideOrientation
import com.braniik.slate.data.guideRegions
import com.braniik.slate.data.halvingGuide
import com.braniik.slate.data.plusUnique
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class GuidePatternTest {

    private fun vertical(at: Float) = GuideLine(orientation = GuideOrientation.VERTICAL, positionDp = at)
    private fun horizontal(at: Float) = GuideLine(orientation = GuideOrientation.HORIZONTAL, positionDp = at)

    @Test
    fun aBareScreenIsOneRegion() {
        val regions = guideRegions(emptyList(), 400f, 800f)
        assertEquals(1, regions.size)
        assertEquals(400f, regions[0].width, 0.01f)
        assertEquals(800f, regions[0].height, 0.01f)
    }

    @Test
    fun twoCentreLinesMakeFourQuadrants() {
        val regions = guideRegions(listOf(vertical(200f), horizontal(400f)), 400f, 800f)
        assertEquals(4, regions.size)
        regions.forEach {
            assertEquals(200f, it.width, 0.01f)
            assertEquals(400f, it.height, 0.01f)
        }
    }

    @Test
    fun guidesOutsideTheScreenAreIgnored() {
        val regions = guideRegions(listOf(vertical(-30f), vertical(900f)), 400f, 800f)
        assertEquals(1, regions.size)
    }

    @Test
    fun halvingLandsOnTheMidpoint() {
        val region = guideRegions(emptyList(), 400f, 800f).single()
        val guide = region.halvingGuide(GuideOrientation.VERTICAL, minSideDp = 56f)
        assertNotNull(guide)
        assertEquals(200f, guide!!.positionDp, 0.01f)
    }

    @Test
    fun regionsTooSmallToAimAtDoNotSplit() {
        val region = guideRegions(emptyList(), 80f, 800f).single()
        assertNull(region.halvingGuide(GuideOrientation.VERTICAL, minSideDp = 56f))
        assertNotNull(region.halvingGuide(GuideOrientation.HORIZONTAL, minSideDp = 56f))
    }

    @Test
    fun halvingTwiceQuartersTheScreen() {
        var guides = emptyList<GuideLine>()
        guides = guides.plusUnique(
            guideRegions(guides, 400f, 800f).single().halvingGuide(GuideOrientation.VERTICAL, 56f)!!
        )
        val leftHalf = guideRegions(guides, 400f, 800f).first()
        guides = guides.plusUnique(leftHalf.halvingGuide(GuideOrientation.VERTICAL, 56f)!!)

        val widths = guideRegions(guides, 400f, 800f).map { it.width }
        assertEquals(listOf(100f, 100f, 200f), widths)
    }

    @Test
    fun aGuideIsNotAddedTwiceOnTheSameSpot() {
        val guides = listOf(vertical(200f))
        assertEquals(1, guides.plusUnique(vertical(200.4f)).size)
        assertEquals(2, guides.plusUnique(vertical(240f)).size)
        assertEquals(2, guides.plusUnique(horizontal(200f)).size)
    }
}