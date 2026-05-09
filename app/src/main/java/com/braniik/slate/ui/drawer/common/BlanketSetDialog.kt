package com.braniik.slate.ui.drawer.common

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
import com.braniik.slate.ui.theme.SlateBackground
import com.braniik.slate.ui.theme.SlateOnBackground
import com.braniik.slate.ui.theme.SlateSubtle

private val sliderColors
    @Composable get() = SliderDefaults.colors(
        thumbColor = SlateOnBackground,
        activeTrackColor = SlateOnBackground,
        inactiveTrackColor = SlateSubtle
    )

private val switchColors
    @Composable get() = SwitchDefaults.colors(
        checkedThumbColor = SlateOnBackground,
        checkedTrackColor = SlateSubtle,
        uncheckedThumbColor = SlateSubtle,
        uncheckedTrackColor = SlateBackground
    )

@Composable
fun BlanketSetDialog(
    isFreescreen: Boolean,
    onDismiss: () -> Unit,
    onApply: (HomeScreenApp.() -> HomeScreenApp) -> Unit
) {
    if (isFreescreen) FreescreenBlanket(onDismiss, onApply)
    else ListBlanket(onDismiss, onApply)
}

@Composable
private fun FreescreenBlanket(
    onDismiss: () -> Unit,
    onApply: (HomeScreenApp.() -> HomeScreenApp) -> Unit
) {
    var iconSize by remember { mutableIntStateOf(56) }
    var showLabel by remember { mutableStateOf(true) }
    var iconShape by remember { mutableStateOf("round") }

    EditDialogShell(
        title = "all apps",
        onDismiss = onDismiss,
        onSave = {
            onApply {
                val shift = (iconSize - iconSizeDp) / 2f
                copy(
                    iconSizeDp = iconSize,
                    showLabel = showLabel,
                    iconShape = iconShape,
                    xPos = xPos - shift,
                    yPos = yPos - shift
                )
            }
        }
    ) {
        OptionLabel("icon size — ${iconSize}dp")
        Slider(
            value = iconSize.toFloat(),
            onValueChange = { iconSize = it.toInt() },
            valueRange = 32f..96f,
            steps = 15,
            colors = sliderColors,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        OptionLabel("icon shape")
        Spacer(Modifier.height(4.dp))
        IconShapePicker(selected = iconShape, onSelect = { iconShape = it })

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
                colors = switchColors
            )
        }
    }
}

@Composable
private fun ListBlanket(
    onDismiss: () -> Unit,
    onApply: (HomeScreenApp.() -> HomeScreenApp) -> Unit
) {
    var textSize by remember { mutableIntStateOf(16) }
    var iconSize by remember { mutableIntStateOf(32) }
    var showIcon by remember { mutableStateOf(true) }
    var iconShape by remember { mutableStateOf("round") }

    EditDialogShell(
        title = "all apps",
        onDismiss = onDismiss,
        onSave = {
            onApply { copy(listTextSizeSp = textSize, listIconSizeDp = iconSize, showLabel = showIcon, iconShape = iconShape) }
        }
    ) {
        OptionLabel("text size — ${textSize}sp")
        Slider(
            value = textSize.toFloat(),
            onValueChange = { textSize = it.toInt() },
            valueRange = 12f..24f,
            steps = 11,
            colors = sliderColors,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        OptionLabel("icon size — ${iconSize}dp")
        Slider(
            value = iconSize.toFloat(),
            onValueChange = { iconSize = it.toInt() },
            valueRange = 20f..64f,
            steps = 10,
            colors = sliderColors,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        OptionLabel("icon shape")
        Spacer(Modifier.height(4.dp))
        IconShapePicker(selected = iconShape, onSelect = { iconShape = it })

        Spacer(Modifier.height(12.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            OptionLabel("show icon")
            Switch(
                checked = showIcon,
                onCheckedChange = { showIcon = it },
                colors = switchColors
            )
        }
    }
}