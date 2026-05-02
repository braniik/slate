package com.braniik.slate.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

enum class GuideOrientation { VERTICAL, HORIZONTAL }

data class GuideLine(
    val id: String = java.util.UUID.randomUUID().toString(),
    val orientation: GuideOrientation,
    val positionDp: Float
)

fun GuideLine.toJson(): JSONObject = JSONObject().apply {
    put("id", id)
    put("orientation", orientation.name)
    put("positionDp", positionDp.toDouble())
}

fun JSONObject.toGuideLine(): GuideLine = GuideLine(
    id = getString("id"),
    orientation = GuideOrientation.valueOf(getString("orientation")),
    positionDp = getDouble("positionDp").toFloat()
)

fun List<GuideLine>.toGuideJsonString(): String {
    val arr = JSONArray()
    forEach { arr.put(it.toJson()) }
    return arr.toString()
}

fun String.toGuideLines(): List<GuideLine> {
    if (isBlank()) return emptyList()
    return try {
        val arr = JSONArray(this)
        (0 until arr.length()).map { arr.getJSONObject(it).toGuideLine() }
    } catch (_: Exception) {
        emptyList()
    }
}

fun Context.guideLinesFlow(): Flow<List<GuideLine>> =
    dataStore.data.map { prefs ->
        (prefs[PreferenceKeys.GUIDE_LINES] ?: "").toGuideLines()
    }

suspend fun Context.saveGuideLines(guides: List<GuideLine>) {
    dataStore.edit { prefs ->
        prefs[PreferenceKeys.GUIDE_LINES] = guides.toGuideJsonString()
    }
}