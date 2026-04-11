package com.braniik.slate.ui.setup

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.braniik.slate.data.LauncherSettings

private val Background = Color(0xFF080808)
private val White = Color(0xFFFFFFFF)
private val Subtle = Color(0xFF444444)

enum class SetupStep { LAYOUT, DETAILS }

@Composable
fun SetupScreen(onFinish: (LauncherSettings) -> Unit) {
    var step by remember { mutableStateOf(SetupStep.LAYOUT) }
    var settings by remember { mutableStateOf(LauncherSettings()) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(targetState = step, label = "setup_step") { currentStep ->
            when (currentStep) {
                SetupStep.LAYOUT -> LayoutStep(
                    selected = settings.layoutMode,
                    onSelect = { mode ->
                        settings = settings.copy(layoutMode = mode)
                        step = SetupStep.DETAILS
                    }
                )
                SetupStep.DETAILS -> if (settings.layoutMode == "grid") {
                    GridDetailsStep(settings = settings, onConfirm = { updated ->
                        onFinish(updated)
                    })
                } else {
                    ListDetailsStep(settings = settings, onConfirm = { updated ->
                        onFinish(updated)
                    })
                }
            }
        }
    }
}

@Composable
private fun LayoutStep(selected: String, onSelect: (String) -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(40.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("slate", fontSize = 36.sp, fontWeight = FontWeight.Light, color = White, letterSpacing = 8.sp)
            Text("how do you like your apps displayed?", fontSize = 14.sp, color = Subtle)
        }

        Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
            LayoutCard(
                label = "grid",
                description = "icons in rows",
                selected = selected == "grid",
                onClick = { onSelect("grid") }
            )
            LayoutCard(
                label = "list",
                description = "list of apps",
                selected = selected == "list",
                onClick = { onSelect("list") }
            )
        }
    }
}

@Composable
private fun LayoutCard(label: String, description: String, selected: Boolean, onClick: () -> Unit) {
    val borderColor = if (selected) White else Subtle
    Column(
        modifier = Modifier
            .width(130.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(label, fontSize = 18.sp, fontWeight = FontWeight.Medium, color = if (selected) White else Subtle)
        Text(description, fontSize = 11.sp, color = Subtle)
    }
}

@Composable
private fun GridDetailsStep(settings: LauncherSettings, onConfirm: (LauncherSettings) -> Unit) {
    var current by remember { mutableStateOf(settings) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        Text("grid options", fontSize = 22.sp, fontWeight = FontWeight.Light, color = White, letterSpacing = 4.sp)

        OptionRow(label = "columns") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(3, 4, 5).forEach { n ->
                    ToggleChip(
                        text = "$n",
                        selected = current.gridColumns == n,
                        onClick = { current = current.copy(gridColumns = n) }
                    )
                }
            }
        }

        OptionRow(label = "app names") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ToggleChip("show", current.gridShowNames) { current = current.copy(gridShowNames = true) }
                ToggleChip("hide", !current.gridShowNames) { current = current.copy(gridShowNames = false) }
            }
        }

        OptionRow(label = "icon size") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("small", "medium", "large").forEach { size ->
                    ToggleChip(size, current.gridIconSize == size) {
                        current = current.copy(gridIconSize = size)
                    }
                }
            }
        }

        ConfirmButton { onConfirm(current) }
    }
}

@Composable
private fun ListDetailsStep(settings: LauncherSettings, onConfirm: (LauncherSettings) -> Unit) {
    var current by remember { mutableStateOf(settings) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        Text("list options", fontSize = 22.sp, fontWeight = FontWeight.Light, color = White, letterSpacing = 4.sp)

        OptionRow(label = "icons") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ToggleChip("show", current.listShowIcons) { current = current.copy(listShowIcons = true) }
                ToggleChip("hide", !current.listShowIcons) { current = current.copy(listShowIcons = false) }
            }
        }

        OptionRow(label = "text size") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("small", "medium", "large").forEach { size ->
                    ToggleChip(size, current.listTextSize == size) {
                        current = current.copy(listTextSize = size)
                    }
                }
            }
        }

        ConfirmButton { onConfirm(current) }
    }
}

@Composable
private fun OptionRow(label: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(label, fontSize = 12.sp, color = Subtle, letterSpacing = 2.sp)
        content()
    }
}

@Composable
private fun ToggleChip(text: String, selected: Boolean, onClick: () -> Unit) {
    val borderColor = if (selected) White else Subtle
    val textColor = if (selected) White else Subtle
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(text, fontSize = 13.sp, color = textColor)
    }
}

@Composable
private fun ConfirmButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = White, contentColor = Background),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("done", fontSize = 14.sp, letterSpacing = 2.sp)
    }
}