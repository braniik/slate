package com.braniik.slate.data

import kotlin.math.abs

// Nothing here is persisted. Halving is a way of *producing* guide lines, not a property of them, so the stored JSON is byte-for-byte what 1.0 wrote.
data class GuideRegion(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float
) {
    val width: Float get() = right - left
    val height: Float get() = bottom - top

    fun contains(x: Float, y: Float): Boolean = x >= left && x <= right && y >= top && y <= bottom

    fun midX(): Float = (left + right) / 2f
    fun midY(): Float = (top + bottom) / 2f
}

fun guideRegions(guides: List<GuideLine>, widthDp: Float, heightDp: Float): List<GuideRegion> {
    if (widthDp <= 0f || heightDp <= 0f) return emptyList()

    val columns = edges(guides, GuideOrientation.VERTICAL, widthDp)
    val rows = edges(guides, GuideOrientation.HORIZONTAL, heightDp)

    return rows.flatMap { (top, bottom) ->
        columns.map { (left, right) -> GuideRegion(left, top, right, bottom) }
    }
}

private fun edges(
    guides: List<GuideLine>,
    orientation: GuideOrientation,
    extentDp: Float
): List<Pair<Float, Float>> =
    (listOf(0f) + guides
        .filter { it.orientation == orientation && it.positionDp > 0f && it.positionDp < extentDp }
        .map { it.positionDp }
        .sorted() + extentDp)
        .zipWithNext()

fun GuideRegion.halvingGuide(orientation: GuideOrientation, minSideDp: Float): GuideLine? =
    when (orientation) {
        GuideOrientation.VERTICAL ->
            if (width / 2f < minSideDp) null
            else GuideLine(orientation = orientation, positionDp = midX())

        GuideOrientation.HORIZONTAL ->
            if (height / 2f < minSideDp) null
            else GuideLine(orientation = orientation, positionDp = midY())
    }

fun List<GuideLine>.plusUnique(guide: GuideLine, epsilonDp: Float = 1f): List<GuideLine> =
    if (any { it.orientation == guide.orientation && abs(it.positionDp - guide.positionDp) < epsilonDp }) this
    else this + guide