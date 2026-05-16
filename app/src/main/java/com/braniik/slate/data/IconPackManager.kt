package com.braniik.slate.data

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.content.res.XmlResourceParser
import android.graphics.drawable.Drawable
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory

data class InstalledIconPack(
    val packageName: String,
    val label: String,
    val icon: Drawable
)

private val ICON_PACK_ACTIONS = listOf(
    "org.adw.launcher.THEMES",
    "com.gau.go.launcherex.theme",
    "com.novalauncher.THEME",
    "com.anddoes.launcher.THEME",
    "com.teslacoilsw.launcher.THEME"
)

fun discoverIconPacks(context: Context): List<InstalledIconPack> {
    val pm = context.packageManager
    val seen = mutableSetOf<String>()
    val packs = mutableListOf<InstalledIconPack>()

    for (action in ICON_PACK_ACTIONS) {
        val results = pm.queryIntentActivities(Intent(action), PackageManager.GET_META_DATA)
        for (ri in results) {
            val pkg = ri.activityInfo.packageName
            if (pkg in seen || pkg == context.packageName) continue
            seen.add(pkg)
            try {
                val appInfo = pm.getApplicationInfo(pkg, 0)
                packs.add(InstalledIconPack(
                    packageName = pkg,
                    label = pm.getApplicationLabel(appInfo).toString(),
                    icon = pm.getApplicationIcon(appInfo)
                ))
            } catch (_: Exception) {}
        }
    }

    return packs.sortedBy { it.label.lowercase() }
}

class IconPackManager(
    private val context: Context,
    private val packPackageName: String
) {
    private val componentMap: Map<String, String>
    private val packResources: Resources?

    init {
        var resources: Resources? = null
        var mapping = emptyMap<String, String>()
        try {
            resources = context.packageManager.getResourcesForApplication(packPackageName)
            mapping = parseAppFilter(resources)
        } catch (_: Exception) {}
        packResources = resources
        componentMap = mapping
    }

    fun getIcon(appPackageName: String): Drawable? {
        val drawableName = componentMap[appPackageName] ?: return null
        val res = packResources ?: return null
        return try {
            val id = res.getIdentifier(drawableName, "drawable", packPackageName)
            @Suppress("DEPRECATION")
            if (id != 0) res.getDrawable(id, null) else null
        } catch (_: Exception) {
            null
        }
    }

    private fun parseAppFilter(resources: Resources): Map<String, String> {
        val map = mutableMapOf<String, String>()

        val xmlId = resources.getIdentifier("appfilter", "xml", packPackageName)
        if (xmlId != 0) {
            try {
                val parser = resources.getXml(xmlId)
                parseXmlResource(parser, map)
                parser.close()
                if (map.isNotEmpty()) return map
            } catch (_: Exception) {}
        }

        val rawId = resources.getIdentifier("appfilter", "raw", packPackageName)
        if (rawId != 0) {
            try {
                resources.openRawResource(rawId).use { stream ->
                    val factory = XmlPullParserFactory.newInstance()
                    val parser = factory.newPullParser()
                    parser.setInput(stream, "UTF-8")
                    parseXmlPull(parser, map)
                }
                if (map.isNotEmpty()) return map
            } catch (_: Exception) {}
        }

        try {
            val pm = context.packageManager
            val assets = pm.getResourcesForApplication(packPackageName).assets
            assets.open("appfilter.xml").use { stream ->
                val factory = XmlPullParserFactory.newInstance()
                val parser = factory.newPullParser()
                parser.setInput(stream, "UTF-8")
                parseXmlPull(parser, map)
            }
        } catch (_: Exception) {}

        return map
    }

    private fun parseXmlResource(parser: XmlResourceParser, map: MutableMap<String, String>) {
        var eventType = parser.eventType
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG && parser.name == "item") {
                val component = parser.getAttributeValue(null, "component")
                val drawable = parser.getAttributeValue(null, "drawable")
                if (component != null && !drawable.isNullOrBlank()) {
                    extractPackageName(component)?.let { pkg ->
                        map.putIfAbsent(pkg, drawable)
                    }
                }
            }
            eventType = parser.next()
        }
    }

    private fun parseXmlPull(parser: XmlPullParser, map: MutableMap<String, String>) {
        var eventType = parser.eventType
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG && parser.name == "item") {
                val component = parser.getAttributeValue(null, "component")
                val drawable = parser.getAttributeValue(null, "drawable")
                if (component != null && !drawable.isNullOrBlank()) {
                    extractPackageName(component)?.let { pkg ->
                        map.putIfAbsent(pkg, drawable)
                    }
                }
            }
            eventType = parser.next()
        }
    }

    companion object {
        private val COMPONENT_REGEX = Regex("""ComponentInfo\{(.+?)/""")

        private fun extractPackageName(component: String): String? =
            COMPONENT_REGEX.find(component)?.groupValues?.get(1)
    }
}