package com.braniik.slate.ui.drawer.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.braniik.slate.data.HomeScreenApp
import com.braniik.slate.ui.theme.SlateBackground
import com.braniik.slate.ui.theme.SlateOnBackground
import com.braniik.slate.ui.theme.SlateSubtle

private val radioButtonColors
    @Composable get() = RadioButtonDefaults.colors(
        selectedColor = SlateOnBackground,
        unselectedColor = SlateSubtle
    )

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
fun FreescreenBlanketSetDialog(
    title: String,
    selectedCount: Int,
    onDismiss: () -> Unit,
    onApply: (distributionAxis: String, transform: HomeScreenApp.() -> HomeScreenApp) -> Unit
) {
    var iconSize by remember { mutableIntStateOf(56) }
    var showLabel by remember { mutableStateOf(true) }
    var iconShape by remember { mutableStateOf("round") }
    var rotation by remember { mutableFloatStateOf(0f) }
    var distribution by remember { mutableStateOf("none") }

    EditDialogShell(
        title = title,
        onDismiss = onDismiss,
        onSave = {
            onApply(distribution) {
                val shift = (iconSize - iconSizeDp) / 2f
                copy(
                    iconSizeDp = iconSize,
                    showLabel = showLabel,
                    iconShape = iconShape,
                    rotationDeg = rotation,
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

        OptionLabel("rotation — ${rotation.toInt()}°")
        Slider(
            value = rotation,
            onValueChange = { rotation = it },
            valueRange = -360f..360f,
            colors = sliderColors,
            modifier = Modifier.fillMaxWidth()
        )

        if (selectedCount >= 3) {
            Spacer(Modifier.height(16.dp))

            OptionLabel("distribute")
            val distributeOptions = listOf("horizontally", "vertically", "none")

            Column(Modifier.selectableGroup()) {
                distributeOptions.forEach { label ->
                    val isSelected = (label == distribution)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp)
                            .selectable(
                                selected = isSelected,
                                onClick = {
                                    distribution = label
                                },
                                role = Role.RadioButton
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = null, // Row handles the click
                            colors = radioButtonColors
                        )
                        Text(
                            text = label,
                            color = if (isSelected) SlateOnBackground else SlateSubtle,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 10.dp)
                        )
                    }
                }
            }
        }

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
fun ListBlanketSetDialog(
    title: String,
    onDismiss: () -> Unit,
    onApply: (HomeScreenApp.() -> HomeScreenApp) -> Unit
) {
    var textSize by remember { mutableIntStateOf(16) }
    var iconSize by remember { mutableIntStateOf(32) }
    var showIcon by remember { mutableStateOf(true) }
    var iconShape by remember { mutableStateOf("round") }
    var rotation by remember { mutableFloatStateOf(0f) }

    EditDialogShell(
        title = title,
        onDismiss = onDismiss,
        onSave = {
            onApply { copy(listTextSizeSp = textSize, listIconSizeDp = iconSize, showLabel = showIcon, iconShape = iconShape, rotationDeg = rotation) }
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

        OptionLabel("rotation — ${rotation.toInt()}°")
        Slider(
            value = rotation,
            onValueChange = { rotation = it },
            valueRange = -360f..360f,
            colors = sliderColors,
            modifier = Modifier.fillMaxWidth()
        )

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