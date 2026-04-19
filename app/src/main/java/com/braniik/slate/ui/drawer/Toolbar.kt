package com.braniik.slate.ui.drawer

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.braniik.slate.ui.theme.SlateOnBackground
import com.braniik.slate.ui.theme.SlateSubtle

@Composable
internal fun Toolbar(mode: HomeMode, onModeChange: (HomeMode) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "slate",
            fontSize = 18.sp,
            fontWeight = FontWeight.Light,
            color = SlateOnBackground,
            letterSpacing = 4.sp
        )

        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            ToolbarIcon(Icons.Filled.Add, "add", mode == HomeMode.ADDING) {
                onModeChange(HomeMode.ADDING)
            }
            ToolbarIcon(Icons.Filled.Edit, "edit", mode == HomeMode.EDITING) {
                onModeChange(HomeMode.EDITING)
            }
            ToolbarIcon(Icons.Filled.Delete, "remove", mode == HomeMode.DELETING) {
                onModeChange(HomeMode.DELETING)
            }
        }
    }
}

@Composable
private fun ToolbarIcon(
    icon: ImageVector,
    label: String,
    active: Boolean,
    onClick: () -> Unit
) {
    val tint by animateColorAsState(
        if (active) SlateOnBackground else SlateSubtle,
        label = "toolbar_$label"
    )
    IconButton(onClick = onClick, modifier = Modifier.size(40.dp)) {
        Icon(icon, contentDescription = label, tint = tint, modifier = Modifier.size(20.dp))
    }
}