package com.braniik.slate.ui.drawer.list

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import com.braniik.slate.data.HomeScreenApp
import com.braniik.slate.ui.drawer.AppInfo
import com.braniik.slate.ui.drawer.HomeMode
import com.braniik.slate.ui.theme.SlateDanger
import com.braniik.slate.ui.theme.SlateOnBackground
import com.braniik.slate.ui.theme.SlateSubtle

@Composable
fun HomeList(
    homeApps: List<HomeScreenApp>,
    allApps: List<AppInfo>,
    mode: HomeMode,
    horizontal: Boolean,
    onTap: (HomeScreenApp) -> Unit,
    onMoveUp: (HomeScreenApp) -> Unit,
    onMoveDown: (HomeScreenApp) -> Unit
) {
    if (horizontal) {
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize()
        ) {
            items(homeApps) { homeApp ->
                val info = allApps.find { it.packageName == homeApp.packageName } ?: return@items
                HorizontalListItem(homeApp, info, mode, onTap)
            }
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(homeApps) { homeApp ->
                val info = allApps.find { it.packageName == homeApp.packageName } ?: return@items
                VerticalListItem(homeApp, info, mode, onTap, onMoveUp, onMoveDown)
            }
        }
    }
}

@Composable
private fun VerticalListItem(
    homeApp: HomeScreenApp,
    info: AppInfo,
    mode: HomeMode,
    onTap: (HomeScreenApp) -> Unit,
    onMoveUp: (HomeScreenApp) -> Unit,
    onMoveDown: (HomeScreenApp) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .then(deleteBorder(mode))
            .clickable { onTap(homeApp) }
            .padding(vertical = 10.dp, horizontal = 4.dp)
    ) {
        if (mode == HomeMode.EDITING) {
            ReorderControls(
                onUp = { onMoveUp(homeApp) },
                onDown = { onMoveDown(homeApp) }
            )
        }

        if (homeApp.showLabel) {
            val iconSize = homeApp.listIconSizeDp.dp
            Image(
                bitmap = info.icon.toBitmap(
                    iconSize.value.toInt(),
                    iconSize.value.toInt()
                ).asImageBitmap(),
                contentDescription = info.label,
                modifier = Modifier.size(iconSize)
            )
            Spacer(Modifier.width(16.dp))
        }

        Text(
            text = info.label,
            fontSize = homeApp.listTextSizeSp.sp,
            color = SlateOnBackground,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun HorizontalListItem(
    homeApp: HomeScreenApp,
    info: AppInfo,
    mode: HomeMode,
    onTap: (HomeScreenApp) -> Unit
) {
    val iconSize = homeApp.listIconSizeDp.dp
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier
            .then(deleteBorder(mode))
            .clickable { onTap(homeApp) }
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        if (homeApp.showLabel) {
            Image(
                bitmap = info.icon.toBitmap(
                    iconSize.value.toInt(),
                    iconSize.value.toInt()
                ).asImageBitmap(),
                contentDescription = info.label,
                modifier = Modifier.size(iconSize)
            )
        }

        Text(
            text = info.label,
            fontSize = homeApp.listTextSizeSp.sp,
            color = SlateOnBackground,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ReorderControls(onUp: () -> Unit, onDown: () -> Unit) {
    Column(
        modifier = Modifier.padding(end = 8.dp),
        verticalArrangement = Arrangement.spacedBy((-8).dp)
    ) {
        IconButton(onClick = onUp, modifier = Modifier.size(28.dp)) {
            Icon(
                Icons.Filled.KeyboardArrowUp, "move up",
                tint = SlateSubtle, modifier = Modifier.size(18.dp)
            )
        }
        IconButton(onClick = onDown, modifier = Modifier.size(28.dp)) {
            Icon(
                Icons.Filled.KeyboardArrowDown, "move down",
                tint = SlateSubtle, modifier = Modifier.size(18.dp)
            )
        }
    }
}

private fun deleteBorder(mode: HomeMode): Modifier =
    if (mode == HomeMode.DELETING) {
        Modifier
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, SlateDanger, RoundedCornerShape(8.dp))
    } else Modifier