package com.braniik.slate.ui.drawer

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import com.braniik.slate.data.LauncherSettings

private val White = Color(0xFFFFFFFF)

data class AppInfo(
    val label: String,
    val packageName: String,
    val icon: Drawable
)

fun loadApps(context: Context): List<AppInfo> {
    val intent = Intent(Intent.ACTION_MAIN).apply {
        addCategory(Intent.CATEGORY_LAUNCHER)
    }
    return context.packageManager
        .queryIntentActivities(intent, 0)
        .sortedBy { it.loadLabel(context.packageManager).toString().lowercase() }
        .map { info ->
            AppInfo(
                label = info.loadLabel(context.packageManager).toString(),
                packageName = info.activityInfo.packageName,
                icon = info.loadIcon(context.packageManager)
            )
        }
}

fun launchApp(context: Context, packageName: String) {
    context.packageManager.getLaunchIntentForPackage(packageName)?.let {
        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(it)
    }
}

@Composable
fun AppDrawerScreen(settings: LauncherSettings) {
    val context = LocalContext.current
    val apps = remember { loadApps(context) }

    if (settings.layoutMode == "grid") {
        AppGrid(apps = apps, settings = settings, context = context)
    } else {
        AppList(apps = apps, settings = settings, context = context)
    }
}

@Composable
private fun AppGrid(apps: List<AppInfo>, settings: LauncherSettings, context: Context) {
    val iconSize: Dp = when (settings.gridIconSize) {
        "small" -> 44.dp
        "large" -> 68.dp
        else -> 56.dp
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(settings.gridColumns),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(apps) { app ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .clickable { launchApp(context, app.packageName) }
                    .padding(vertical = 8.dp)
            ) {
                Image(
                    bitmap = app.icon.toBitmap(iconSize.value.toInt(), iconSize.value.toInt()).asImageBitmap(),
                    contentDescription = app.label,
                    modifier = Modifier.size(iconSize)
                )
                if (settings.gridShowNames) {
                    Text(
                        text = app.label,
                        fontSize = 10.sp,
                        color = White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun AppList(apps: List<AppInfo>, settings: LauncherSettings, context: Context) {
    val textSize = when (settings.listTextSize) {
        "small" -> 13.sp
        "large" -> 19.sp
        else -> 16.sp
    }

    LazyColumn(
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(apps) { app ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { launchApp(context, app.packageName) }
                    .padding(vertical = 10.dp)
            ) {
                if (settings.listShowIcons) {
                    Image(
                        bitmap = app.icon.toBitmap(40, 40).asImageBitmap(),
                        contentDescription = app.label,
                        modifier = Modifier.size(36.dp)
                    )
                }
                Text(
                    text = app.label,
                    fontSize = textSize,
                    color = White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}