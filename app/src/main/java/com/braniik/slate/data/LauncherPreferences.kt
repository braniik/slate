package com.braniik.slate.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "launcher_prefs")

object PreferenceKeys {
    val SETUP_DONE = booleanPreferencesKey("setup_done")
    val LAYOUT_MODE = stringPreferencesKey("layout_mode")
    val HOME_SCREEN_APPS = stringPreferencesKey("home_screen_apps")
}

data class LauncherSettings(
    val setupDone: Boolean = false,
    val layoutMode: String = ""
)

data class HomeScreenApp(
    val packageName: String,
    val iconSizeDp: Int = 56,
    val showLabel: Boolean = true,
    val listTextSizeSp: Int = 16,
    val xPos: Float = 0f,
    val yPos: Float = 0f,
    val order: Int = 0
)

fun HomeScreenApp.toJson(): JSONObject = JSONObject().apply {
    put("packageName", packageName)
    put("iconSizeDp", iconSizeDp)
    put("showLabel", showLabel)
    put("listTextSizeSp", listTextSizeSp)
    put("xPos", xPos.toDouble())
    put("yPos", yPos.toDouble())
    put("order", order)
}

fun JSONObject.toHomeScreenApp(): HomeScreenApp = HomeScreenApp(
    packageName = getString("packageName"),
    iconSizeDp = optInt("iconSizeDp", 56),
    showLabel = optBoolean("showLabel", true),
    listTextSizeSp = optInt("listTextSizeSp", 16),
    xPos = optDouble("xPos", 0.0).toFloat(),
    yPos = optDouble("yPos", 0.0).toFloat(),
    order = optInt("order", 0)
)

fun List<HomeScreenApp>.toJsonString(): String {
    val arr = JSONArray()
    forEach { arr.put(it.toJson()) }
    return arr.toString()
}

fun String.toHomeScreenApps(): List<HomeScreenApp> {
    if (isBlank()) return emptyList()
    return try {
        val arr = JSONArray(this)
        (0 until arr.length()).map { arr.getJSONObject(it).toHomeScreenApp() }
    } catch (_: Exception) {
        emptyList()
    }
}

fun Context.launcherSettingsFlow(): Flow<LauncherSettings> =
    dataStore.data.map { prefs ->
        val raw = prefs[PreferenceKeys.LAYOUT_MODE] ?: ""
        val mode = if (raw == "grid") "freescreen" else raw
        LauncherSettings(
            setupDone = prefs[PreferenceKeys.SETUP_DONE] ?: false,
            layoutMode = mode
        )
    }

fun Context.homeScreenAppsFlow(): Flow<List<HomeScreenApp>> =
    dataStore.data.map { prefs ->
        (prefs[PreferenceKeys.HOME_SCREEN_APPS] ?: "").toHomeScreenApps()
    }

suspend fun Context.saveSettings(settings: LauncherSettings) {
    dataStore.edit { prefs ->
        prefs[PreferenceKeys.SETUP_DONE] = true
        prefs[PreferenceKeys.LAYOUT_MODE] = settings.layoutMode
    }
}

suspend fun Context.saveHomeScreenApps(apps: List<HomeScreenApp>) {
    dataStore.edit { prefs ->
        prefs[PreferenceKeys.HOME_SCREEN_APPS] = apps.toJsonString()
    }
}