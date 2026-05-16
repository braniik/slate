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
    val LIST_ORIENTATION = stringPreferencesKey("list_orientation")
    val TOOLBAR_POSITION = stringPreferencesKey("toolbar_position")
    val HOME_SCREEN_APPS = stringPreferencesKey("home_screen_apps")
    val WALLPAPER = stringPreferencesKey("wallpaper_config")
    val GUIDE_LINES = stringPreferencesKey("guide_lines")
    val ICON_PACK = stringPreferencesKey("icon_pack")
}

data class LauncherSettings(
    val setupDone: Boolean = false,
    val layoutMode: String = "",
    val listOrientation: String = "vertical",
    val toolbarPosition: String = "top"
)

data class HomeScreenApp(
    val packageName: String,
    val iconSizeDp: Int = 56,
    val showLabel: Boolean = true,
    val listTextSizeSp: Int = 16,
    val listIconSizeDp: Int = 32,
    val iconShape: String = "round",
    val xPos: Float = 0f,
    val yPos: Float = 0f,
    val order: Int = 0
)

fun HomeScreenApp.toJson(): JSONObject = JSONObject().apply {
    put("packageName", packageName)
    put("iconSizeDp", iconSizeDp)
    put("showLabel", showLabel)
    put("listTextSizeSp", listTextSizeSp)
    put("listIconSizeDp", listIconSizeDp)
    put("iconShape", iconShape)
    put("xPos", xPos.toDouble())
    put("yPos", yPos.toDouble())
    put("order", order)
}

fun JSONObject.toHomeScreenApp(): HomeScreenApp = HomeScreenApp(
    packageName = getString("packageName"),
    iconSizeDp = optInt("iconSizeDp", 56),
    showLabel = optBoolean("showLabel", true),
    listTextSizeSp = optInt("listTextSizeSp", 16),
    listIconSizeDp = optInt("listIconSizeDp", 32),
    iconShape = optString("iconShape", "round"),
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
            layoutMode = mode,
            listOrientation = prefs[PreferenceKeys.LIST_ORIENTATION] ?: "vertical",
            toolbarPosition = prefs[PreferenceKeys.TOOLBAR_POSITION] ?: "top"
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
        prefs[PreferenceKeys.LIST_ORIENTATION] = settings.listOrientation
    }
}

suspend fun Context.saveHomeScreenApps(apps: List<HomeScreenApp>) {
    dataStore.edit { prefs ->
        prefs[PreferenceKeys.HOME_SCREEN_APPS] = apps.toJsonString()
    }
}

suspend fun Context.saveLayoutMode(mode: String) {
    dataStore.edit { prefs ->
        prefs[PreferenceKeys.LAYOUT_MODE] = mode
    }
}

suspend fun Context.saveListOrientation(orientation: String) {
    dataStore.edit { prefs ->
        prefs[PreferenceKeys.LIST_ORIENTATION] = orientation
    }
}

suspend fun Context.saveToolbarPosition(position: String) {
    dataStore.edit { prefs ->
        prefs[PreferenceKeys.TOOLBAR_POSITION] = position
    }
}

fun Context.wallpaperConfigFlow(): Flow<WallpaperConfig> =
    dataStore.data.map { prefs ->
        WallpaperConfig.fromJson(prefs[PreferenceKeys.WALLPAPER] ?: "")
    }

suspend fun Context.saveWallpaperConfig(config: WallpaperConfig) {
    dataStore.edit { prefs ->
        prefs[PreferenceKeys.WALLPAPER] = config.toJson()
    }
}

fun Context.iconPackFlow(): Flow<String> =
    dataStore.data.map { prefs -> prefs[PreferenceKeys.ICON_PACK] ?: "" }

suspend fun Context.saveIconPack(packPackageName: String) {
    dataStore.edit { prefs ->
        prefs[PreferenceKeys.ICON_PACK] = packPackageName
    }
}