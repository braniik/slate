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
    private val light = 0xFFF2F2F2.toInt()
    private val dark = 0xFF101010.toInt()
    private val edges = listOf("top", "bottom", "left", "right")

    private fun edge(config: WallpaperConfig, systemColor: Int?, at: String) =
        resolveEdgeForeground(config, null, systemColor, 1080, 2400, at)

    @Test
    fun lightSystemWallpaperGetsDarkTextEverywhere() {
        edges.forEach { assertEquals(DarkText, edge(system, light, it)) }
        assertEquals(DarkText, resolveContentForeground(system, light))
    }

    @Test
    fun darkSystemWallpaperGetsLightTextEverywhere() {
        edges.forEach { assertEquals(LightText, edge(system, dark, it)) }
        assertEquals(LightText, resolveContentForeground(system, dark))
    }

    @Test
    fun aWallpaperWithoutColoursReadsAsDark() {
        assertEquals(LightText, edge(system, null, "top"))
        assertEquals(LightText, resolveContentForeground(system, null))
    }

    @Test
    fun systemModeIgnoresTheStoredSolidColour() {
        val leftover = WallpaperConfig(mode = "system", solidColor = light)
        assertEquals(LightText, resolveContentForeground(leftover, dark))
    }

    @Test
    fun otherModesIgnoreTheSystemColour() {
        val solidLight = WallpaperConfig(mode = "solid", solidColor = light)
        assertEquals(DarkText, resolveContentForeground(solidLight, dark))
        assertEquals(DarkText, edge(solidLight, dark, "top"))
    }
}