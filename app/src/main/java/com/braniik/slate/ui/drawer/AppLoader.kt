package com.braniik.slate.ui.drawer

import android.content.Context
import android.content.pm.LauncherApps
import android.content.pm.PackageManager
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
import kotlinx.coroutines.withContext

class AppInfo(
    val label: String,
    val packageName: String,
    val userSerial: Long,
    val user: UserHandle,
    private val icon: Drawable,
    private val badge: ((Bitmap, Int) -> Bitmap)?
) {
    val key: String get() = "$packageName:$userSerial"

    private val rendered = HashMap<Int, ImageBitmap>()

    fun bitmap(sizePx: Int): ImageBitmap = synchronized(rendered) {
        rendered.getOrPut(sizePx) {
            val bitmap = icon.toUnmaskedBitmap(sizePx)
            (badge?.invoke(bitmap, sizePx) ?: bitmap).asImageBitmap()
        }
    }
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
        val pm: PackageManager = context.packageManager
        val launcherApps = context.getSystemService(LauncherApps::class.java)
            ?: return@withContext emptyList()
        val userManager = context.getSystemService(UserManager::class.java)
            ?: return@withContext emptyList()
        val myUser = Process.myUserHandle()

        launcherApps.profiles.flatMap { user ->
            val serial = userManager.getSerialNumberForUser(user)
            val badge: ((Bitmap, Int) -> Bitmap)? =
                if (user != myUser) { bitmap, sizePx ->
                    pm.getUserBadgedIcon(bitmap.toDrawable(context.resources), user)
                        .toBitmap(sizePx, sizePx)
                } else null
            launcherApps.getActivityList(null, user).map { activity ->
                val pkg = activity.applicationInfo.packageName
                AppInfo(
                    label = activity.label.toString(),
                    packageName = pkg,
                    userSerial = serial,
                    user = user,
                    icon = iconPackManager?.getIcon(pkg) ?: activity.getIcon(0),
                    badge = badge
                )
            }
        }
            .distinctBy { it.key }
            .sortedBy { it.label.lowercase() }
    }