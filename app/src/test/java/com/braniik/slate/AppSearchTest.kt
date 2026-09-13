package com.braniik.slate

import com.braniik.slate.ui.drawer.matchRank
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
}