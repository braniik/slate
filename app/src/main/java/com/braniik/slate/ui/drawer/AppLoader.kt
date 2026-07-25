package com.braniik.slate.ui.drawer

import android.content.Context
import android.content.pm.LauncherApps
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.Drawable
import android.os.Process
import android.os.UserHandle
import android.os.UserManager
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import com.braniik.slate.data.IconPackManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

private const val MAX_ICON_SIZE_DP = 96

data class AppInfo(
    val label: String,
    val packageName: String,
    val userSerial: Long,
    val user: UserHandle,
    val icon: ImageBitmap
) {
    val key: String get() = "$packageName:$userSerial"
}

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
        val launcherApps = context.getSystemService(LauncherApps::class.java)
            ?: return@withContext emptyList()
        val userManager = context.getSystemService(UserManager::class.java)
            ?: return@withContext emptyList()
        val myUser = Process.myUserHandle()
        val iconSizePx = (MAX_ICON_SIZE_DP * context.resources.displayMetrics.density).toInt()

        coroutineScope {
            launcherApps.profiles.flatMap { user ->
                val serial = userManager.getSerialNumberForUser(user)
                launcherApps.getActivityList(null, user).map { activity ->
                    async {
                        val pkg = activity.applicationInfo.packageName
                        val drawable = iconPackManager?.getIcon(pkg) ?: activity.getIcon(0)
                        var bitmap = drawable.toUnmaskedBitmap(iconSizePx)
                        if (user != myUser) {
                            bitmap = pm
                                .getUserBadgedIcon(bitmap.toDrawable(context.resources), user)
                                .toBitmap(iconSizePx, iconSizePx)
                        }
                        AppInfo(
                            label = activity.label.toString(),
                            packageName = pkg,
                            userSerial = serial,
                            user = user,
                            icon = bitmap.asImageBitmap()
                        )
                    }
                }
            }.awaitAll()
        }
            .distinctBy { it.key }
            .sortedBy { it.label.lowercase() }
    }