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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.braniik.slate.data.HomeScreenApp
import com.braniik.slate.data.key
import com.braniik.slate.data.LauncherSettings
import com.braniik.slate.data.packageChangesFlow
import com.braniik.slate.data.WallpaperConfig
import com.braniik.slate.data.applySystemWallpaper
import com.braniik.slate.data.pruneWallpaperImages
import com.braniik.slate.data.guideLinesFlow
import com.braniik.slate.data.HomeAppsStore
import com.braniik.slate.data.iconPackFlow
import com.braniik.slate.data.saveGuideLines
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun AppDrawerScreen(
    settings: LauncherSettings,
    homeResetSignal: Int = 0
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp.toFloat()
    val screenHeightDp = configuration.screenHeightDp.toFloat()

    val selectedIconPack by remember { context.iconPackFlow() }.collectAsState(initial = "")
    var packageGeneration by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        context.packageChangesFlow().collect { packageGeneration++ }
    }
    var allApps by remember { mutableStateOf<List<AppInfo>?>(null) }
    LaunchedEffect(selectedIconPack, packageGeneration) {
        allApps = loadApps(context, selectedIconPack)
    }

    val homeAppsStore = remember { HomeAppsStore(context, scope) }
    val homeApps by homeAppsStore.apps.collectAsState()

    LaunchedEffect(allApps) {
        val apps = allApps ?: return@LaunchedEffect
        if (apps.isEmpty()) return@LaunchedEffect
        val installedKeys = apps.mapTo(HashSet()) { it.key }
        val visibleSerials = apps.mapTo(HashSet()) { it.userSerial }
        val byPackage = apps.groupBy { it.packageName }
        homeAppsStore.update { stored ->
            stored.mapNotNull { app ->
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
        }
    }

    val wallpaperConfig by remember { context.wallpaperConfigFlow() }.collectAsState(initial = WallpaperConfig())
    val guideLines by remember { context.guideLinesFlow() }.collectAsState(initial = emptyList())
    val ui = rememberHomeUiState()

    LaunchedEffect(homeResetSignal) {
        if (homeResetSignal > 0) ui.reset()
    }

    BackHandler { ui.dismissTopmost() }

    fun switchMode(targetMode: String) {
        homeAppsStore.update { stored ->
            if (targetMode == "freescreen") {
                stored.mapIndexed { i, app ->
                    val (x, y) = gridFreescreenPos(i, screenWidthDp, screenHeightDp)
                    app.copy(xPos = x, yPos = y)
                }
            } else {
                stored.mapIndexed { i, app -> app.copy(order = i) }
            }
        }
        scope.launch { context.saveLayoutMode(targetMode) }
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
                            homeAppsStore.update { stored ->
                                if (stored.any { it.key == info.key }) stored
                                else {
                                    val (x, y) = centerFreescreenPos(screenWidthDp, screenHeightDp)
                                    stored + HomeScreenApp(
                                        packageName = info.packageName,
                                        userSerial = info.userSerial,
                                        order = stored.size,
                                        xPos = x,
                                        yPos = y
                                    )
                                }
                            }
                        },
                        onClose = { ui.mode = HomeMode.NORMAL }
                    )
                }

                settings.layoutMode == "freescreen" -> HomeFreescreen(
                    homeApps = homeApps,
                    allApps = allApps.orEmpty(),
                    mode = ui.mode,
                    guideLines = guideLines,
                    onTap = { app -> handleAppTap(app, ui.mode, context, homeAppsStore::update) { ui.editingKey = it.key } },
                    onLongPress = onLongPress,
                    onPositionChanged = { app, newX, newY ->
                        homeAppsStore.update { stored ->
                            stored.map {
                                if (it.key == app.key) it.copy(xPos = newX, yPos = newY) else it
                            }
                        }
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
                        onTap = { app -> handleAppTap(app, ui.mode, context, homeAppsStore::update) { ui.editingKey = it.key } },
                        onLongPress = onLongPress,
                        onReorder = { reordered ->
                            val orderByKey = reordered.associate { it.key to it.order }
                            homeAppsStore.update { stored ->
                                stored.map { it.copy(order = orderByKey[it.key] ?: it.order) }
                            }
                        }
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
            val onSave: (HomeScreenApp) -> Unit = { edited ->
                homeAppsStore.update { stored ->
                    stored.map { current ->
                        if (current.key != edited.key) current
                        else {
                            val shift = (edited.iconSizeDp - current.iconSizeDp) / 2f
                            edited.copy(
                                xPos = current.xPos - shift,
                                yPos = current.yPos - shift,
                                order = current.order
                            )
                        }
                    }
                }
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
                    homeAppsStore.update { stored -> stored.map { it.transform() } }
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
                        withContext(Dispatchers.IO) {
                            pruneWallpaperImages(context, setOf(config.imagePath))
                        }
                    }
                    ui.showWallpaperPicker = false
                },
                onDismiss = {
                    scope.launch(Dispatchers.IO) {
                        pruneWallpaperImages(context, setOf(wallpaperConfig.imagePath))
                    }
                    ui.showWallpaperPicker = false
                }
            )
        }
    }
}

private fun handleAppTap(
    app: HomeScreenApp,
    mode: HomeMode,
    context: android.content.Context,
    update: ((List<HomeScreenApp>) -> List<HomeScreenApp>) -> Unit,
    openEdit: (HomeScreenApp) -> Unit
) {
    when (mode) {
        HomeMode.NORMAL -> launchApp(context, app.packageName, app.userSerial)
        HomeMode.EDITING -> openEdit(app)
        HomeMode.DELETING -> update { stored -> stored.filter { it.key != app.key } }
        HomeMode.ADDING -> {}
    }
}

private const val FOOTPRINT_DP = 72f
private const val MARGIN_DP = 16f

internal fun centerFreescreenPos(
    widthDp: Float,
    heightDp: Float,
    footprintDp: Float = FOOTPRINT_DP
): Pair<Float, Float> =
    ((widthDp - footprintDp) / 2f).coerceAtLeast(0f) to
            ((heightDp - footprintDp) / 2f).coerceAtLeast(0f)

internal fun gridFreescreenPos(
    index: Int,
    widthDp: Float,
    heightDp: Float,
    footprintDp: Float = FOOTPRINT_DP
): Pair<Float, Float> {
    val stepX = footprintDp + 18f
    val stepY = footprintDp + 38f
    val cols = (((widthDp - 2 * MARGIN_DP) / stepX).toInt()).coerceAtLeast(1)
    val rows = (((heightDp - 2 * MARGIN_DP) / stepY).toInt()).coerceAtLeast(1)
    val slot = index % (cols * rows)
    return (MARGIN_DP + (slot % cols) * stepX) to (MARGIN_DP + (slot / cols) * stepY)
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