package com.braniik.slate.ui.drawer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import com.braniik.slate.data.LauncherSettings
import com.braniik.slate.data.homeScreenAppsFlow
import com.braniik.slate.data.saveHomeScreenApps
import com.braniik.slate.ui.drawer.freescreen.FreescreenEditDialog
import com.braniik.slate.ui.drawer.freescreen.HomeFreescreen
import com.braniik.slate.ui.drawer.list.HomeList
import com.braniik.slate.ui.drawer.list.ListEditDialog
import com.braniik.slate.ui.theme.SlateSubtle
import kotlinx.coroutines.launch
@Composable
fun AppDrawerScreen(settings: LauncherSettings) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val allApps = remember { loadApps(context) }

    val homeApps by context.homeScreenAppsFlow().collectAsState(initial = emptyList())
    var mode by remember { mutableStateOf(HomeMode.NORMAL) }
    var editingApp by remember { mutableStateOf<HomeScreenApp?>(null) }

    fun save(apps: List<HomeScreenApp>) {
        scope.launch { context.saveHomeScreenApps(apps) }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Toolbar(
                mode = mode,
                onModeChange = { newMode ->
                    mode = if (mode == newMode) HomeMode.NORMAL else newMode
                }
            )

            when {
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
                    onTap = { app -> handleAppTap(app, mode, context, homeApps, ::save) { editingApp = it } },
                    onPositionChanged = { app, newX, newY ->
                        save(homeApps.map {
                            if (it.packageName == app.packageName) it.copy(xPos = newX, yPos = newY) else it
                        })
                    }
                )

                else -> {
                    val sortedApps = homeApps.sortedBy { it.order }
                    HomeList(
                        homeApps = sortedApps,
                        allApps = allApps,
                        mode = mode,
                        horizontal = settings.listOrientation == "horizontal",
                        onTap = { app -> handleAppTap(app, mode, context, homeApps, ::save) { editingApp = it } },
                        onMoveUp = { app ->
                            val idx = sortedApps.indexOf(app)
                            if (idx > 0) save(swapOrder(sortedApps, idx, idx - 1))
                        },
                        onMoveDown = { app ->
                            val idx = sortedApps.indexOf(app)
                            if (idx < sortedApps.lastIndex) save(swapOrder(sortedApps, idx, idx + 1))
                        }
                    )
                }
            }
        }

        editingApp?.let { app ->
            val info = allApps.find { it.packageName == app.packageName } ?: return@let
            val onSave: (HomeScreenApp) -> Unit = { updated ->
                save(homeApps.map { if (it.packageName == updated.packageName) updated else it })
                editingApp = null
            }
            if (settings.layoutMode == "freescreen") {
                FreescreenEditDialog(app, info, onDismiss = { editingApp = null }, onSave = onSave)
            } else {
                ListEditDialog(app, info, onDismiss = { editingApp = null }, onSave = onSave)
            }
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
private fun swapOrder(apps: List<HomeScreenApp>, i: Int, j: Int): List<HomeScreenApp> {
    val result = apps.toMutableList()
    val a = result[i]
    val b = result[j]
    result[i] = a.copy(order = b.order)
    result[j] = b.copy(order = a.order)
    return result
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