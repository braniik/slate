package com.braniik.slate.data

import android.content.Context
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeAppsStore(context: Context, private val scope: CoroutineScope) {

    private val appContext = context.applicationContext

    private val _apps = MutableStateFlow<List<HomeScreenApp>>(emptyList())
    val apps: StateFlow<List<HomeScreenApp>> = _apps.asStateFlow()

    private val seeded = CompletableDeferred<Unit>()

    init {
        scope.launch {
            _apps.value = appContext.homeScreenAppsFlow().first()
            seeded.complete(Unit)
            _apps.collect { appContext.saveHomeScreenApps(it) }
        }
    }

    fun update(transform: (List<HomeScreenApp>) -> List<HomeScreenApp>) {
        if (seeded.isCompleted) {
            _apps.update(transform)
        } else {
            scope.launch {
                seeded.await()
                _apps.update(transform)
            }
        }
    }
}
