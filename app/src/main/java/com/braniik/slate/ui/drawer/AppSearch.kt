package com.braniik.slate.ui.drawer

fun matchRank(label: String, packageName: String, query: String): Int? {
    val needle = query.trim().lowercase()
    if (needle.isEmpty()) return 0
    val haystack = label.lowercase()
    return when {
        haystack.startsWith(needle) -> 0
        haystack.hasWordStartingWith(needle) -> 1
        haystack.contains(needle) -> 2
        haystack.hasInOrder(needle) -> 3
        packageName.lowercase().contains(needle) -> 4
        else -> null
    }
}

private fun String.hasWordStartingWith(needle: String): Boolean {
    var space = indexOf(' ')
    while (space >= 0) {
        if (startsWith(needle, space + 1)) return true
        space = indexOf(' ', space + 1)
    }
    return false
}

private fun String.hasInOrder(needle: String): Boolean {
    var at = 0
    for (c in needle) {
        if (c == ' ') continue
        at = indexOf(c, at)
        if (at < 0) return false
        at++
    }
    return true
}

internal fun List<AppInfo>.matching(query: String): List<AppInfo> {
    if (query.isBlank()) return this
    return mapNotNull { app -> matchRank(app.label, app.packageName, query)?.let { rank -> rank to app } }
        .sortedWith(compareBy({ it.first }, { it.second.label.lowercase() }))
        .map { it.second }
}

internal fun <T> soleMatch(query: String, matches: List<T>): T? =
    if (query.isBlank()) null else matches.singleOrNull()