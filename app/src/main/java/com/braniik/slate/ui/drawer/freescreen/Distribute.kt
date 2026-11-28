package com.braniik.slate.ui.drawer.freescreen

import com.braniik.slate.data.HomeScreenApp

private fun fromCenter(center: Float, app: HomeScreenApp): Float {
    return center - footprintOf(app) / 2f
}

private fun toCenter(start: Float, app: HomeScreenApp): Float {
    return start + footprintOf(app) / 2f
}

fun distribute(axis: String, apps: List<HomeScreenApp>) : List<HomeScreenApp> {
    if (("none" == axis) || (apps.size < 3))
        return apps

    // dynamic getter/"setter", based on axis
    val (getPos, withPos) = when (axis) {
        "horizontally" -> Pair(
            { app: HomeScreenApp -> toCenter(app.xPos, app) },
            { app: HomeScreenApp, newX: Float -> app.copy(xPos = fromCenter(newX, app)) }
        )
        "vertically" -> Pair(
            { app: HomeScreenApp -> toCenter(app.yPos, app) },
            { app: HomeScreenApp, newY: Float -> app.copy(yPos = fromCenter(newY, app)) }
        )
        else -> throw IllegalArgumentException("Unknown axis: $axis")
    }

    val sortedApps = apps
        // sort by relevant coordinate position
        .sortedBy(getPos)
    val n = sortedApps.size - 1
    val firstPos = getPos(sortedApps.first())
    val spacing = (getPos(sortedApps.last()) - firstPos) / n

    return sortedApps.mapIndexed { i, app ->
        // skip first and last app in the list
        if (i in 1 until n)
            withPos(app, firstPos + (i * spacing))
        else
            app
    }
}