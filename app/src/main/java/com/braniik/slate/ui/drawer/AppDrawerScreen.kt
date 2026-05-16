package com.braniik.slate.ui.drawer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.braniik.slate.data.HomeScreenApp
import com.braniik.slate.data.IconPackManager
import com.braniik.slate.data.LauncherSettings
import com.braniik.slate.data.WallpaperConfig
import com.braniik.slate.data.guideLinesFlow
import com.braniik.slate.data.homeScreenAppsFlow
import com.braniik.slate.data.iconPackFlow
import com.braniik.slate.data.saveGuideLines
import com.braniik.slate.data.saveHomeScreenApps
import com.braniik.slate.data.saveIconPack
import com.braniik.slate.data.saveLayoutMode
import com.braniik.slate.data.saveListOrientation
import com.braniik.slate.data.saveToolbarPosition
import com.braniik.slate.data.saveWallpaperConfig
import com.braniik.slate.data.wallpaperConfigFlow
import com.braniik.slate.ui.drawer.common.BlanketSetDialog
import com.braniik.slate.ui.drawer.freescreen.FreescreenEditDialog
import com.braniik.slate.ui.drawer.freescreen.HomeFreescreen
import com.braniik.slate.ui.drawer.list.HomeList
import com.braniik.slate.ui.drawer.list.ListEditDialog
import com.braniik.slate.ui.drawer.settings.SlateSettingsSheet
import com.braniik.slate.ui.drawer.settings.WallpaperPicker
import com.braniik.slate.ui.theme.SlateSubtle
import kotlinx.coroutines.launch

@Composable
fun AppDrawerScreen(settings: LauncherSettings) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val selectedIconPack by context.iconPackFlow().collectAsState(initial = "")
    val iconPackManager = remember(selectedIconPack) {
        if (selectedIconPack.isNotBlank()) IconPackManager(context, selectedIconPack) else null
    }
    val allApps = remember(iconPackManager) { loadApps(context, iconPackManager) }

    val homeApps by context.homeScreenAppsFlow().collectAsState(initial = emptyList())
    val wallpaperConfig by context.wallpaperConfigFlow().collectAsState(initial = WallpaperConfig())
    val guideLines by context.guideLinesFlow().collectAsState(initial = emptyList())
    var mode by remember { mutableStateOf(HomeMode.NORMAL) }
    var editingPkg by remember { mutableStateOf<String?>(null) }
    var showSettings by remember { mutableStateOf(false) }
    var showBlanketSet by remember { mutableStateOf(false) }
    var showWallpaperPicker by remember { mutableStateOf(false) }

    fun save(apps: List<HomeScreenApp>) {
        scope.launch { context.saveHomeScreenApps(apps) }
    }

    fun switchMode(targetMode: String) {
        android.util.Log.d("Slate", "switchMode called with: $targetMode")
        scope.launch {
            android.util.Log.d("Slate", "coroutine entered, homeApps count: ${homeApps.size}")
            val resetApps = if (targetMode == "freescreen") {
                homeApps.mapIndexed { i, app ->
                    val (x, y) = nextFreescreenPos(i)
                    app.copy(xPos = x, yPos = y)
                }
            } else {
                homeApps.mapIndexed { i, app -> app.copy(order = i) }
            }
            android.util.Log.d("Slate", "saving ${resetApps.size} apps, then mode=$targetMode")
            context.saveHomeScreenApps(resetApps)
            context.saveLayoutMode(targetMode)
            android.util.Log.d("Slate", "save complete")
        }
        showSettings = false
        android.util.Log.d("Slate", "showSettings set to false")
    }

    val pos = settings.toolbarPosition
    val sideToolbar = pos == "left" || pos == "right"

    val toolbar: @Composable () -> Unit = {
        Toolbar(
            mode = mode,
            showSettings = showSettings,
            position = pos,
            onModeChange = { newMode ->
                showSettings = false
                mode = if (mode == newMode) HomeMode.NORMAL else newMode
            },
            onSettingsToggle = {
                showSettings = !showSettings
                if (showSettings) mode = HomeMode.NORMAL
            },
            onBlanketSet = { showBlanketSet = true }
        )
    }

    val content: @Composable (Modifier) -> Unit = { modifier ->
        Box(modifier = modifier) {
            when {
                showSettings -> {
                    SlateSettingsSheet(
                        layoutMode = settings.layoutMode,
                        listOrientation = settings.listOrientation,
                        toolbarPosition = pos,
                        wallpaperConfig = wallpaperConfig,
                        selectedIconPack = selectedIconPack,
                        onSwitchMode = ::switchMode,
                        onListOrientationChange = { scope.launch { context.saveListOrientation(it) } },
                        onToolbarPositionChange = { scope.launch { context.saveToolbarPosition(it) } },
                        onIconPackChange = { scope.launch { context.saveIconPack(it) } },
                        onOpenWallpaperPicker = { showWallpaperPicker = true },
                        onClose = { showSettings = false }
                    )
                }

                homeApps.isEmpty() && mode != HomeMode.ADDING -> EmptyState()

                mode == HomeMode.ADDING -> {
                    val existing = homeApps.map { it.packageName }.toSet()
                    val available = allApps.filter { it.packageName !in existing }
                    AddAppsOverlay(
                        apps = available,
                        onAdd = { info ->
                            val (x, y) = nextFreescreenPos(homeApps.size)
                            save(
                                homeApps + HomeScreenApp(
                                    packageName = info.packageName,
                                    order = homeApps.size,
                                    xPos = x,
                                    yPos = y
                                )
                            )
                        },
                        onClose = { mode = HomeMode.NORMAL }
                    )
                }

                settings.layoutMode == "freescreen" -> HomeFreescreen(
                    homeApps = homeApps,
                    allApps = allApps,
                    mode = mode,
                    guideLines = guideLines,
                    onTap = { app -> handleAppTap(app, mode, context, homeApps, ::save) { editingPkg = it.packageName } },
                    onPositionChanged = { app, newX, newY ->
                        save(homeApps.map {
                            if (it.packageName == app.packageName) it.copy(xPos = newX, yPos = newY) else it
                        })
                    },
                    onGuidesChanged = { updatedGuides ->
                        scope.launch { context.saveGuideLines(updatedGuides) }
                    }
                )

                else -> {
                    val sortedApps = homeApps.sortedBy { it.order }
                    HomeList(
                        homeApps = sortedApps,
                        allApps = allApps,
                        mode = mode,
                        horizontal = settings.listOrientation == "horizontal",
                        onTap = { app -> handleAppTap(app, mode, context, homeApps, ::save) { editingPkg = it.packageName } },
                        onReorder = ::save
                    )
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (sideToolbar) {
            Row(modifier = Modifier.fillMaxSize()) {
                if (pos == "left") toolbar()
                content(Modifier.weight(1f))
                if (pos == "right") toolbar()
            }
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                if (pos == "top") toolbar()
                content(Modifier.weight(1f))
                if (pos == "bottom") toolbar()
            }
        }

        editingPkg?.let { pkg ->
            val app = homeApps.find { it.packageName == pkg } ?: run { editingPkg = null; return@let }
            val info = allApps.find { it.packageName == pkg } ?: return@let
            val onSave: (HomeScreenApp) -> Unit = { updated ->
                save(homeApps.map { if (it.packageName == updated.packageName) updated else it })
                editingPkg = null
            }
            if (settings.layoutMode == "freescreen") {
                FreescreenEditDialog(app, info, onDismiss = { editingPkg = null }, onSave = onSave)
            } else {
                ListEditDialog(app, info, onDismiss = { editingPkg = null }, onSave = onSave)
            }
        }

        if (showBlanketSet) {
            BlanketSetDialog(
                isFreescreen = settings.layoutMode == "freescreen",
                onDismiss = { showBlanketSet = false },
                onApply = { transform ->
                    save(homeApps.map { it.transform() })
                    showBlanketSet = false
                }
            )
        }

        if (showWallpaperPicker) {
            WallpaperPicker(
                current = wallpaperConfig,
                onSave = { config ->
                    scope.launch { context.saveWallpaperConfig(config) }
                    showWallpaperPicker = false
                },
                onDismiss = { showWallpaperPicker = false }
            )
        }
    }
}

private fun handleAppTap(
    app: HomeScreenApp,
    mode: HomeMode,
    context: android.content.Context,
    homeApps: List<HomeScreenApp>,
    save: (List<HomeScreenApp>) -> Unit,
    openEdit: (HomeScreenApp) -> Unit
) {
    when (mode) {
        HomeMode.NORMAL -> launchApp(context, app.packageName)
        HomeMode.EDITING -> openEdit(app)
        HomeMode.DELETING -> save(homeApps.filter { it.packageName != app.packageName })
        HomeMode.ADDING -> {}
    }
}

private fun nextFreescreenPos(count: Int): Pair<Float, Float> {
    val cols = 4
    val stepX = 90f
    val stepY = 110f
    return (16f + (count % cols) * stepX) to (16f + (count / cols) * stepY)
}

@Composable
private fun EmptyState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("nothing here yet", fontSize = 16.sp, color = SlateSubtle)
            Text("tap + to add your apps", fontSize = 12.sp, color = SlateSubtle)
        }
    }
}