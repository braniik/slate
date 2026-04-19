package com.braniik.slate.ui.drawer.freescreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.braniik.slate.data.HomeScreenApp
import com.braniik.slate.ui.drawer.AppInfo
import com.braniik.slate.ui.drawer.common.EditDialogShell
import com.braniik.slate.ui.drawer.common.OptionLabel
import com.braniik.slate.ui.theme.SlateBackground
import com.braniik.slate.ui.theme.SlateOnBackground
import com.braniik.slate.ui.theme.SlateSubtle

@Composable
fun FreescreenEditDialog(
    app: HomeScreenApp,
    info: AppInfo,
    onDismiss: () -> Unit,
    onSave: (HomeScreenApp) -> Unit
) {
    var iconSize by remember { mutableIntStateOf(app.iconSizeDp) }
    var showLabel by remember { mutableStateOf(app.showLabel) }

    EditDialogShell(
        title = info.label,
        onDismiss = onDismiss,
        onSave = { onSave(app.copy(iconSizeDp = iconSize, showLabel = showLabel)) }
    ) {
        OptionLabel("icon size — ${iconSize}dp")
        Slider(
            value = iconSize.toFloat(),
            onValueChange = { iconSize = it.toInt() },
            valueRange = 32f..96f,
            steps = 15,
            colors = SliderDefaults.colors(
                thumbColor = SlateOnBackground,
                activeTrackColor = SlateOnBackground,
                inactiveTrackColor = SlateSubtle
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            OptionLabel("show name")
            Switch(
                checked = showLabel,
                onCheckedChange = { showLabel = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = SlateOnBackground,
                    checkedTrackColor = SlateSubtle,
                    uncheckedThumbColor = SlateSubtle,
                    uncheckedTrackColor = SlateBackground
                )
            )
        }
    }
}