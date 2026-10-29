package com.braniik.slate

import com.braniik.slate.ui.drawer.matchRank
import com.braniik.slate.ui.drawer.soleMatch
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AppSearchTest {

    @Test
    fun emptyQueryMatchesEverythingEqually() {
        assertEquals(0, matchRank("Signal", "org.thoughtcrime.securesms", "  "))
    }

    @Test
    fun prefixOutranksWordStartOutranksSubstring() {
        val prefix = matchRank("Signal", "a", "sig")!!
        val wordStart = matchRank("Open Signal", "b", "sig")!!
        val substring = matchRank("Insignal", "c", "sig")!!
        assertTrue(prefix < wordStart)
        assertTrue(wordStart < substring)
    }

    @Test
    fun packageNameIsTheLastResort() {
        val byPackage = matchRank("Messages", "org.thoughtcrime.securesms", "thoughtcrime")!!
        val byLabel = matchRank("Messages", "x", "mess")!!
        assertTrue(byLabel < byPackage)
    }

    @Test
    fun caseIsIgnoredOnBothSides() {
        assertEquals(0, matchRank("Signal", "x", "SIGNAL"))
    }

    @Test
    fun noMatchIsNull() {
        assertNull(matchRank("Signal", "org.thoughtcrime.securesms", "firefox"))
    }

    @Test
    fun lettersInOrderMatchFuzzily() {
        assertEquals(3, matchRank("Syncthing", "x", "sth"))
        assertEquals(3, matchRank("F-Droid", "x", "fdroid"))
        assertEquals(3, matchRank("Google Maps", "x", "g m"))
    }

    @Test
    fun lettersOutOfOrderDoNotMatch() {
        assertNull(matchRank("Syncthing", "x", "hts"))
    }

    @Test
    fun fuzzyRanksBelowSubstringAndAbovePackage() {
        val substring = matchRank("Insignal", "a", "sig")!!
        val fuzzy = matchRank("Syncthing", "b", "sth")!!
        val byPackage = matchRank("Messages", "org.thoughtcrime.securesms", "thoughtcrime")!!
        assertTrue(substring < fuzzy)
        assertTrue(fuzzy < byPackage)
    }

    @Test
    fun onlyASingleMatchIsSole() {
        assertEquals("signal", soleMatch("sig", listOf("signal")))
        assertNull(soleMatch("s", listOf("signal", "syncthing")))
        assertNull(soleMatch("zzz", emptyList<String>()))
    }

    @Test
    fun aBlankQueryNeverLaunches() {
        assertNull(soleMatch(" ", listOf("signal")))
    }
}