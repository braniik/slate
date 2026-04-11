package com.braniik.slate.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "launcher_prefs")

object PreferenceKeys {
    val SETUP_DONE = booleanPreferencesKey("setup_done")
    val LAYOUT_MODE = stringPreferencesKey("layout_mode")
    val GRID_COLUMNS = intPreferencesKey("grid_columns")
    val GRID_SHOW_NAMES = booleanPreferencesKey("grid_show_names")
    val GRID_ICON_SIZE = stringPreferencesKey("grid_icon_size")
    val LIST_SHOW_ICONS = booleanPreferencesKey("list_show_icons")
    val LIST_TEXT_SIZE = stringPreferencesKey("list_text_size")
}

data class LauncherSettings(
    val setupDone: Boolean = false,
    val layoutMode: String = "grid",
    val gridColumns: Int = 4,
    val gridShowNames: Boolean = true,
    val gridIconSize: String = "medium",
    val listShowIcons: Boolean = true,
    val listTextSize: String = "medium"
)

fun Context.launcherSettingsFlow(): Flow<LauncherSettings> =
    dataStore.data.map { prefs ->
        LauncherSettings(
            setupDone = prefs[PreferenceKeys.SETUP_DONE] ?: false,
            layoutMode = prefs[PreferenceKeys.LAYOUT_MODE] ?: "grid",
            gridColumns = prefs[PreferenceKeys.GRID_COLUMNS] ?: 4,
            gridShowNames = prefs[PreferenceKeys.GRID_SHOW_NAMES] ?: true,
            gridIconSize = prefs[PreferenceKeys.GRID_ICON_SIZE] ?: "medium",
            listShowIcons = prefs[PreferenceKeys.LIST_SHOW_ICONS] ?: true,
            listTextSize = prefs[PreferenceKeys.LIST_TEXT_SIZE] ?: "medium"
        )
    }

suspend fun Context.saveSettings(settings: LauncherSettings) {
    dataStore.edit { prefs ->
        prefs[PreferenceKeys.SETUP_DONE] = true
        prefs[PreferenceKeys.LAYOUT_MODE] = settings.layoutMode
        prefs[PreferenceKeys.GRID_COLUMNS] = settings.gridColumns
        prefs[PreferenceKeys.GRID_SHOW_NAMES] = settings.gridShowNames
        prefs[PreferenceKeys.GRID_ICON_SIZE] = settings.gridIconSize
        prefs[PreferenceKeys.LIST_SHOW_ICONS] = settings.listShowIcons
        prefs[PreferenceKeys.LIST_TEXT_SIZE] = settings.listTextSize
    }
}