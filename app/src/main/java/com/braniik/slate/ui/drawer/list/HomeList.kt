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
    onTap: (HomeScreenApp) -> Unit,
    onMoveUp: (HomeScreenApp) -> Unit,
    onMoveDown: (HomeScreenApp) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(homeApps) { homeApp ->
            val info = allApps.find { it.packageName == homeApp.packageName } ?: return@items

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        if (mode == HomeMode.DELETING) {
                            Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, SlateDanger, RoundedCornerShape(8.dp))
                        } else Modifier
                    )
                    .clickable { onTap(homeApp) }
                    .padding(vertical = 10.dp, horizontal = 4.dp)
            ) {
                if (mode == HomeMode.EDITING) {
                    Column(
                        modifier = Modifier.padding(end = 8.dp),
                        verticalArrangement = Arrangement.spacedBy((-8).dp)
                    ) {
                        IconButton(
                            onClick = { onMoveUp(homeApp) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                Icons.Filled.KeyboardArrowUp, "move up",
                                tint = SlateSubtle, modifier = Modifier.size(18.dp)
                            )
                        }
                        IconButton(
                            onClick = { onMoveDown(homeApp) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                Icons.Filled.KeyboardArrowDown, "move down",
                                tint = SlateSubtle, modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                if (homeApp.showLabel) {
                    Image(
                        bitmap = info.icon.toBitmap(36, 36).asImageBitmap(),
                        contentDescription = info.label,
                        modifier = Modifier.size(32.dp)
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
    }
}