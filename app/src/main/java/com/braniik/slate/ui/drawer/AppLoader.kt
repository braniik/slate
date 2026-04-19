package com.braniik.slate.ui.drawer

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable

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