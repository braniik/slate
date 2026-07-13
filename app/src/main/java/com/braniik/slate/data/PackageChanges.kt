package com.braniik.slate.data

import android.content.Context
import android.content.pm.LauncherApps
import android.os.Handler
import android.os.Looper
import android.os.UserHandle
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

fun Context.packageChangesFlow(): Flow<Unit> = callbackFlow {
    val launcherApps = getSystemService(LauncherApps::class.java)
        ?: run { close(); return@callbackFlow }

    val callback = object : LauncherApps.Callback() {
        override fun onPackageAdded(packageName: String?, user: UserHandle?) { trySend(Unit) }
        override fun onPackageRemoved(packageName: String?, user: UserHandle?) { trySend(Unit) }
        override fun onPackageChanged(packageName: String?, user: UserHandle?) { trySend(Unit) }
        override fun onPackagesAvailable(
            packageNames: Array<out String>?, user: UserHandle?, replacing: Boolean
        ) { trySend(Unit) }
        override fun onPackagesUnavailable(
            packageNames: Array<out String>?, user: UserHandle?, replacing: Boolean
        ) { trySend(Unit) }
    }

    launcherApps.registerCallback(callback, Handler(Looper.getMainLooper()))
    awaitClose { launcherApps.unregisterCallback(callback) }
}