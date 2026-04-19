package com.braniik.slate.ui.drawer

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import com.braniik.slate.ui.theme.SlateOnBackground
import com.braniik.slate.ui.theme.SlateSubtle

@Composable
internal fun AddAppsOverlay(
    apps: List<AppInfo>,
    onAdd: (AppInfo) -> Unit,
    onClose: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("add apps", fontSize = 14.sp, color = SlateSubtle, letterSpacing = 2.sp)
            IconButton(onClick = onClose, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Filled.Close, "close", tint = SlateSubtle, modifier = Modifier.size(18.dp))
            }
        }

        if (apps.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("all apps added", fontSize = 14.sp, color = SlateSubtle)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(apps) { app ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onAdd(app) }
                            .padding(vertical = 10.dp, horizontal = 4.dp)
                    ) {
                        Image(
                            bitmap = app.icon.toBitmap(36, 36).asImageBitmap(),
                            contentDescription = app.label,
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            app.label,
                            fontSize = 14.sp,
                            color = SlateOnBackground,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}