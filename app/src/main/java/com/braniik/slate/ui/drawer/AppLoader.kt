package com.braniik.slate.ui.drawer

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.Drawable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import com.braniik.slate.data.IconPackManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val MAX_ICON_SIZE_DP = 96

data class AppInfo(
    val label: String,
    val packageName: String,
    val icon: ImageBitmap
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

suspend fun loadApps(context: Context, iconPackPackage: String): List<AppInfo> =
    withContext(Dispatchers.IO) {
        val iconPackManager =
            if (iconPackPackage.isNotBlank()) IconPackManager(context, iconPackPackage) else null
        val pm = context.packageManager
        val iconSizePx = (MAX_ICON_SIZE_DP * context.resources.displayMetrics.density).toInt()
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        pm.queryIntentActivities(intent, 0)
            .map { info ->
                val pkg = info.activityInfo.packageName
                val drawable = iconPackManager?.getIcon(pkg) ?: info.loadIcon(pm)
                AppInfo(
                    label = info.loadLabel(pm).toString(),
                    packageName = pkg,
                    icon = drawable.toUnmaskedBitmap(iconSizePx).asImageBitmap()
                )
            }
            .sortedBy { it.label.lowercase() }
    }