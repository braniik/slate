package com.braniik.slate.data

import android.app.role.RoleManager
import android.content.Context
import android.content.Intent

fun isDefaultLauncher(context: Context): Boolean =
    context.getSystemService(RoleManager::class.java)
        ?.isRoleHeld(RoleManager.ROLE_HOME) == true

fun requestDefaultLauncherIntent(context: Context): Intent? =
    context.getSystemService(RoleManager::class.java)
        ?.takeIf { it.isRoleAvailable(RoleManager.ROLE_HOME) && !it.isRoleHeld(RoleManager.ROLE_HOME) }
        ?.createRequestRoleIntent(RoleManager.ROLE_HOME)