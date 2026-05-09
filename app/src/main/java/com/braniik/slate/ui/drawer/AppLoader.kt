package com.braniik.slate.ui.drawer

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.toBitmap

data class AppInfo(
    val label: String,
    val packageName: String,
    val icon: Drawable
)

fun Drawable.toUnmaskedBitmap(sizePx: Int): Bitmap {
    if (this is AdaptiveIconDrawable) {
        val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val inset = sizePx / 4
        val layerBounds = Rect(-inset, -inset, sizePx + inset, sizePx + inset)
        background?.apply { bounds = layerBounds; draw(canvas) }
        foreground?.apply { bounds = layerBounds; draw(canvas) }
        return bitmap
    }
    return toBitmap(sizePx, sizePx)
}

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