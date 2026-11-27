package com.braniik.slate

import com.braniik.slate.data.DarkText
import com.braniik.slate.data.LightText
import com.braniik.slate.data.WallpaperConfig
import com.braniik.slate.data.resolveContentForeground
import com.braniik.slate.data.resolveEdgeForeground
import org.junit.Assert.assertEquals
import org.junit.Test

class SystemWallpaperContrastTest {

    private val system = WallpaperConfig(mode = "system")
    private val lightColor = 0xFFF2F2F2.toInt()
    private val edges = listOf("top", "bottom", "left", "right")

    private fun edge(config: WallpaperConfig, systemDarkText: Boolean?, at: String) =
        resolveEdgeForeground(config, null, systemDarkText, 1080, 2400, at)

    @Test
    fun aWallpaperThatPrefersDarkTextGetsItEverywhere() {
        edges.forEach { assertEquals(DarkText, edge(system, true, it)) }
        assertEquals(DarkText, resolveContentForeground(system, true))
    }

    @Test
    fun aWallpaperThatDoesNotGetsLightTextEverywhere() {
        edges.forEach { assertEquals(LightText, edge(system, false, it)) }
        assertEquals(LightText, resolveContentForeground(system, false))
    }

    @Test
    fun anUnknownWallpaperReadsAsDark() {
        assertEquals(LightText, edge(system, null, "top"))
        assertEquals(LightText, resolveContentForeground(system, null))
    }

    @Test
    fun systemModeIgnoresTheStoredSolidColour() {
        val leftover = WallpaperConfig(mode = "system", solidColor = lightColor)
        assertEquals(LightText, resolveContentForeground(leftover, false))
    }

    @Test
    fun otherModesIgnoreTheSystemWallpaper() {
        val solidLight = WallpaperConfig(mode = "solid", solidColor = lightColor)
        assertEquals(DarkText, resolveContentForeground(solidLight, false))
        assertEquals(DarkText, edge(solidLight, false, "top"))
    }
}