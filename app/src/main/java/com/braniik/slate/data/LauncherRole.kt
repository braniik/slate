package com.braniik.slate.data

import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings

private val HOME_INTENT: Intent
    get() = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME)

fun isDefaultLauncher(context: Context): Boolean =
    context.getSystemService(RoleManager::class.java)
        ?.isRoleHeld(RoleManager.ROLE_HOME) == true

fun requestDefaultLauncherIntent(context: Context): Intent? =
    context.getSystemService(RoleManager::class.java)
        ?.takeIf { it.isRoleAvailable(RoleManager.ROLE_HOME) && !it.isRoleHeld(RoleManager.ROLE_HOME) }
        ?.createRequestRoleIntent(RoleManager.ROLE_HOME)

fun installedHomeAppCount(context: Context): Int =
    context.packageManager
        .queryIntentActivities(HOME_INTENT, PackageManager.MATCH_DEFAULT_ONLY)
        .map { it.activityInfo.packageName }
        .distinct()
        .size

fun currentHomeAppLabel(context: Context): String? {
    val pm = context.packageManager
    val resolved = pm.resolveActivity(HOME_INTENT, PackageManager.MATCH_DEFAULT_ONLY) ?: return null
    if (resolved.activityInfo.packageName == "android") return null
    return runCatching { resolved.loadLabel(pm).toString() }.getOrNull()
}

fun homeSettingsIntent(context: Context): Intent? =
    listOf(
        Intent(Settings.ACTION_HOME_SETTINGS),
        Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS),
        Intent(Settings.ACTION_SETTINGS)
    ).firstOrNull { it.resolveActivity(context.packageManager) != null }
        ?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)