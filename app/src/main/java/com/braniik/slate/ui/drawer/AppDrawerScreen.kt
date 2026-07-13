package com.braniik.slate.ui.drawer

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
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
import com.braniik.slate.data.key
import com.braniik.slate.data.LauncherSettings
import com.braniik.slate.data.packageChangesFlow
import com.braniik.slate.data.WallpaperConfig
import com.braniik.slate.data.applySystemWallpaper
import com.braniik.slate.data.guideLinesFlow
import com.braniik.slate.data.homeScreenAppsFlow
import com.braniik.slate.data.iconPackFlow
import com.braniik.slate.data.saveGuideLines
import com.braniik.slate.data.saveHomeScreenApps
import com.braniik.slate.data.saveIconPack
import com.braniik.slate.data.saveLayoutMode
import com.braniik.slate.data.saveListOrientation
import com.braniik.slate.data.saveToolbarPosition
import com.braniik.slate.data.saveToolbarTitle
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
fun AppDrawerScreen(
    settings: LauncherSettings,
    homeResetSignal: Int = 0
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val selectedIconPack by context.iconPackFlow().collectAsState(initial = "")
    var packageGeneration by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        context.packageChangesFlow().collect { packageGeneration++ }
    }
    var allApps by remember { mutableStateOf<List<AppInfo>?>(null) }
    LaunchedEffect(selectedIconPack, packageGeneration) {
        allApps = loadApps(context, selectedIconPack)
    }

    val homeApps by context.homeScreenAppsFlow().collectAsState(initial = emptyList())

    LaunchedEffect(allApps, homeApps) {
        val apps = allApps ?: return@LaunchedEffect
        if (apps.isEmpty()) return@LaunchedEffect
        val installedKeys = apps.mapTo(HashSet()) { it.key }
        val visibleSerials = apps.mapTo(HashSet()) { it.userSerial }
        val byPackage = apps.groupBy { it.packageName }
        val migrated = homeApps.mapNotNull { app ->
            when {
                // still installed under the same profile
                app.key in installedKeys -> app
                // profile is visible but the app is gone: prune
                app.userSerial in visibleSerials -> null
                // stale serial (pre-0.8 layout)
                else -> byPackage[app.packageName].orEmpty().singleOrNull()
                    ?.let { app.copy(userSerial = it.userSerial) } ?: app
            }
        }
        if (migrated != homeApps) context.saveHomeScreenApps(migrated)
    }

    val wallpaperConfig by context.wallpaperConfigFlow().collectAsState(initial = WallpaperConfig())
    val guideLines by context.guideLinesFlow().collectAsState(initial = emptyList())
    val ui = rememberHomeUiState()

    LaunchedEffect(homeResetSignal) {
        if (homeResetSignal > 0) ui.reset()
    }

    BackHandler { ui.dismissTopmost() }

    fun save(apps: List<HomeScreenApp>) {
        scope.launch { context.saveHomeScreenApps(apps) }
    }

    fun switchMode(targetMode: String) {
        scope.launch {
            val resetApps = if (targetMode == "freescreen") {
                homeApps.mapIndexed { i, app ->
                    val (x, y) = nextFreescreenPos(i)
                    app.copy(xPos = x, yPos = y)
                }
            } else {
                homeApps.mapIndexed { i, app -> app.copy(order = i) }
            }
            context.saveHomeScreenApps(resetApps)
            context.saveLayoutMode(targetMode)
        }
        ui.showSettings = false
    }

    val onLongPress: (HomeScreenApp) -> Unit = { app ->
        if (ui.mode == HomeMode.NORMAL) openAppInfo(context, app.packageName, app.userSerial)
    }

    val pos = settings.toolbarPosition
    val sideToolbar = pos == "left" || pos == "right"

    val toolbar: @Composable () -> Unit = {
        Toolbar(
            mode = ui.mode,
            showSettings = ui.showSettings,
            position = pos,
            title = settings.toolbarTitle,
            onModeChange = { newMode ->
                ui.showSettings = false
                ui.mode = if (ui.mode == newMode) HomeMode.NORMAL else newMode
            },
            onSettingsToggle = {
                ui.showSettings = !ui.showSettings
                if (ui.showSettings) ui.mode = HomeMode.NORMAL
            },
            onBlanketSet = { ui.showBlanketSet = true }
        )
    }

    val content: @Composable (Modifier) -> Unit = { modifier ->
        Box(modifier = modifier) {
            when {
                ui.showSettings -> {
                    SlateSettingsSheet(
                        layoutMode = settings.layoutMode,
                        listOrientation = settings.listOrientation,
                        toolbarPosition = pos,
                        toolbarTitle = settings.toolbarTitle,
                        wallpaperConfig = wallpaperConfig,
                        selectedIconPack = selectedIconPack,
                        onSwitchMode = ::switchMode,
                        onListOrientationChange = { scope.launch { context.saveListOrientation(it) } },
                        onToolbarPositionChange = { scope.launch { context.saveToolbarPosition(it) } },
                        onToolbarTitleChange = { scope.launch { context.saveToolbarTitle(it) } },
                        onIconPackChange = { scope.launch { context.saveIconPack(it) } },
                        onOpenWallpaperPicker = { ui.showWallpaperPicker = true },
                        onClose = { ui.showSettings = false }
                    )
                }

                allApps != null && homeApps.isEmpty() && ui.mode != HomeMode.ADDING -> EmptyState()

                ui.mode == HomeMode.ADDING -> {
                    val existing = homeApps.map { it.key }.toSet()
                    val available = allApps.orEmpty().filter { it.key !in existing }
                    AddAppsOverlay(
                        apps = available,
                        onAdd = { info ->
                            val (x, y) = nextFreescreenPos(homeApps.size)
                            save(
                                homeApps + HomeScreenApp(
                                    packageName = info.packageName,
                                    userSerial = info.userSerial,
                                    order = homeApps.size,
                                    xPos = x,
                                    yPos = y
                                )
                            )
                        },
                        onClose = { ui.mode = HomeMode.NORMAL }
                    )
                }

                settings.layoutMode == "freescreen" -> HomeFreescreen(
                    homeApps = homeApps,
                    allApps = allApps.orEmpty(),
                    mode = ui.mode,
                    guideLines = guideLines,
                    onTap = { app -> handleAppTap(app, ui.mode, context, homeApps, ::save) { ui.editingKey = it.key } },
                    onLongPress = onLongPress,
                    onPositionChanged = { app, newX, newY ->
                        save(homeApps.map {
                            if (it.key == app.key) it.copy(xPos = newX, yPos = newY) else it
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
                        allApps = allApps.orEmpty(),
                        mode = ui.mode,
                        horizontal = settings.listOrientation == "horizontal",
                        onTap = { app -> handleAppTap(app, ui.mode, context, homeApps, ::save) { ui.editingKey = it.key } },
                        onLongPress = onLongPress,
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

        ui.editingKey?.let { editKey ->
            val app = homeApps.find { it.key == editKey } ?: run { ui.editingKey = null; return@let }
            val info = allApps?.find { it.key == editKey } ?: return@let
            val onSave: (HomeScreenApp) -> Unit = { updated ->
                save(homeApps.map { if (it.key == updated.key) updated else it })
                ui.editingKey = null
            }
            if (settings.layoutMode == "freescreen") {
                FreescreenEditDialog(app, info, onDismiss = { ui.editingKey = null }, onSave = onSave)
            } else {
                ListEditDialog(app, info, onDismiss = { ui.editingKey = null }, onSave = onSave)
            }
        }

        if (ui.showBlanketSet) {
            BlanketSetDialog(
                isFreescreen = settings.layoutMode == "freescreen",
                onDismiss = { ui.showBlanketSet = false },
                onApply = { transform ->
                    save(homeApps.map { it.transform() })
                    ui.showBlanketSet = false
                }
            )
        }

        if (ui.showWallpaperPicker) {
            WallpaperPicker(
                current = wallpaperConfig,
                onSave = { config ->
                    scope.launch {
                        context.saveWallpaperConfig(config)
                        applySystemWallpaper(context, config)
                    }
                    ui.showWallpaperPicker = false
                },
                onDismiss = { ui.showWallpaperPicker = false }
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
        HomeMode.NORMAL -> launchApp(context, app.packageName, app.userSerial)
        HomeMode.EDITING -> openEdit(app)
        HomeMode.DELETING -> save(homeApps.filter { it.key != app.key })
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