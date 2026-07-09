package com.braniik.slate.ui.drawer

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.braniik.slate.data.LocalToolbarTextColor

@Composable
internal fun Toolbar(
    mode: HomeMode,
    showSettings: Boolean,
    position: String,
    title: String,
    onModeChange: (HomeMode) -> Unit,
    onSettingsToggle: () -> Unit,
    onBlanketSet: () -> Unit
) {
    val vertical = position == "left" || position == "right"

    if (vertical) {
        VerticalToolbar(mode, showSettings, position, title, onModeChange, onSettingsToggle, onBlanketSet)
    } else {
        HorizontalToolbar(mode, showSettings, position, title, onModeChange, onSettingsToggle, onBlanketSet)
    }
}

@Composable
private fun HorizontalToolbar(
    mode: HomeMode,
    showSettings: Boolean,
    position: String,
    title: String,
    onModeChange: (HomeMode) -> Unit,
    onSettingsToggle: () -> Unit,
    onBlanketSet: () -> Unit
) {
    val textColor = LocalToolbarTextColor.current
    val insets = if (position == "bottom") Modifier.navigationBarsPadding()
    else Modifier.statusBarsPadding()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(insets)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (title.isNotBlank()) {
            Text(
                title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Light,
                color = textColor,
                letterSpacing = 4.sp
            )
        } else {
            Spacer(Modifier)
        }

        Row {
            ToolbarIcon(Icons.Filled.Settings, "settings", showSettings) {
                onSettingsToggle()
            }
            ToolbarIcon(Icons.Filled.Add, "add", mode == HomeMode.ADDING) {
                onModeChange(HomeMode.ADDING)
            }
            ToolbarIcon(Icons.Filled.Edit, "edit", mode == HomeMode.EDITING) {
                onModeChange(HomeMode.EDITING)
            }
            if (mode == HomeMode.EDITING) {
                ToolbarIcon(Icons.Filled.Tune, "blanket set", false) {
                    onBlanketSet()
                }
            }
            ToolbarIcon(Icons.Filled.Delete, "remove", mode == HomeMode.DELETING) {
                onModeChange(HomeMode.DELETING)
            }
        }
    }
}

@Composable
private fun VerticalToolbar(
    mode: HomeMode,
    showSettings: Boolean,
    position: String,
    title: String,
    onModeChange: (HomeMode) -> Unit,
    onSettingsToggle: () -> Unit,
    onBlanketSet: () -> Unit
) {
    val textColor = LocalToolbarTextColor.current
    val rotation = if (position == "left") -90f else 90f

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 4.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (title.isNotBlank()) {
            Text(
                title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Light,
                color = textColor,
                letterSpacing = 4.sp,
                modifier = Modifier.rotate(rotation)
            )
        } else {
            Spacer(Modifier)
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ToolbarIcon(Icons.Filled.Settings, "settings", showSettings) {
                onSettingsToggle()
            }
            ToolbarIcon(Icons.Filled.Add, "add", mode == HomeMode.ADDING) {
                onModeChange(HomeMode.ADDING)
            }
            ToolbarIcon(Icons.Filled.Edit, "edit", mode == HomeMode.EDITING) {
                onModeChange(HomeMode.EDITING)
            }
            if (mode == HomeMode.EDITING) {
                ToolbarIcon(Icons.Filled.Tune, "blanket set", false) {
                    onBlanketSet()
                }
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
    val foreground = LocalToolbarTextColor.current
    val tint by animateColorAsState(
        if (active) foreground else foreground.copy(alpha = 0.55f),
        label = "toolbar_$label"
    )
    IconButton(onClick = onClick, modifier = Modifier.size(48.dp)) {
        Icon(icon, contentDescription = label, tint = tint, modifier = Modifier.size(20.dp))
    }
}