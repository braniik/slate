package com.braniik.slate.ui.drawer

import android.content.ComponentName
import android.content.Context
import android.content.pm.LauncherApps
import android.os.UserHandle
import android.os.UserManager

private class ResolvedActivity(
    val launcherApps: LauncherApps,
    val component: ComponentName,
    val user: UserHandle
)

private fun resolve(context: Context, packageName: String, userSerial: Long): ResolvedActivity? {
    val launcherApps = context.getSystemService(LauncherApps::class.java) ?: return null
    val user = context.getSystemService(UserManager::class.java)
        ?.getUserForSerialNumber(userSerial) ?: return null
    val activity = launcherApps.getActivityList(packageName, user).firstOrNull() ?: return null
    return ResolvedActivity(launcherApps, activity.componentName, user)
}

fun launchApp(context: Context, packageName: String, userSerial: Long) {
    val r = resolve(context, packageName, userSerial) ?: return
    runCatching { r.launcherApps.startMainActivity(r.component, r.user, null, null) }
}

fun openAppInfo(context: Context, packageName: String, userSerial: Long) {
    val r = resolve(context, packageName, userSerial) ?: return
    runCatching { r.launcherApps.startAppDetailsActivity(r.component, r.user, null, null) }
}